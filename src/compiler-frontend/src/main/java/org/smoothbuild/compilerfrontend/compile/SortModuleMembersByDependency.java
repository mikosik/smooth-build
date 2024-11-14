package org.smoothbuild.compilerfrontend.compile;

import static java.lang.String.join;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.graph.SortTopologically.sortTopologically;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.graph.GraphEdge;
import org.smoothbuild.common.graph.GraphNode;
import org.smoothbuild.common.graph.SortTopologically.TopologicalSortingRes;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.base.Nal;

/**
 * Sort module Evaluables and Structs based on dependencies between them.
 */
public class SortModuleMembersByDependency implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var label = COMPILER_FRONT_LABEL.append("sortMembers");
    var sortedTs = sortStructsByDeps(pModule.structs());
    if (sortedTs.sorted() == null) {
      var error = createCycleError("Type hierarchy", sortedTs.cycle());
      return output(label, list(error));
    }
    var sortedEvaluables = sortEvaluablesByDeps(pModule.evaluables());
    if (sortedEvaluables.sorted() == null) {
      var error = createCycleError("Reference graph", sortedEvaluables.cycle());
      return output(label, list(error));
    }
    PModule result = new PModule(
        pModule.name(),
        sortedTs.valuesReversed(),
        sortedEvaluables.valuesReversed(),
        pModule.scope());
    return output(result, label, list());
  }

  private static TopologicalSortingRes<String, PNamedEvaluable, Location> sortEvaluablesByDeps(
      List<PNamedEvaluable> evaluables) {
    HashSet<String> names = new HashSet<>();
    evaluables.forEach(r -> names.add(r.name()));

    HashSet<GraphNode<String, PNamedEvaluable, Location>> nodes = new HashSet<>();
    nodes.addAll(evaluables.map(r -> evaluable(r, names)));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, PNamedEvaluable, Location> evaluable(
      PNamedEvaluable evaluable, Set<String> names) {
    Set<GraphEdge<Location, String>> deps = new HashSet<>();
    new PModuleVisitor() {
      @Override
      public void visitReference(PReference pReference) {
        super.visitReference(pReference);
        if (names.contains(pReference.referencedName())) {
          deps.add(new GraphEdge<>(pReference.location(), pReference.referencedName()));
        }
      }
    }.visitNamedEvaluable(evaluable);
    return new GraphNode<>(evaluable.name(), evaluable, listOfAll(deps));
  }

  private static TopologicalSortingRes<String, PStruct, Location> sortStructsByDeps(
      List<PStruct> structs) {
    var structNames = structs.map(Nal::name).toSet();
    var nodes = structs.map(struct -> structToGraphNode(struct, structNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, PStruct, Location> structToGraphNode(
      PStruct struct, Set<String> funcNames) {
    Set<GraphEdge<Location, String>> deps = new HashSet<>();
    new PModuleVisitor() {
      @Override
      public void visitStructSignature(PStruct pStruct) {
        super.visitStructSignature(pStruct);
        pStruct.fields().forEach(f -> addToDeps(f.type()));
      }

      private void addToDeps(PType pType) {
        switch (pType) {
          case PArrayType arrayT -> addToDeps(arrayT.elemT());
          case PFuncType funcT -> {
            addToDeps(funcT.result());
            funcT.params().forEach(this::addToDeps);
          }
          default -> {
            if (funcNames.contains(pType.name())) {
              deps.add(new GraphEdge<>(pType.location(), pType.name()));
            }
          }
        }
      }
    }.visitStruct(struct);
    return new GraphNode<>(struct.name(), struct, listOfAll(deps));
  }

  private static Log createCycleError(String name, List<GraphEdge<Location, String>> cycle) {
    // Choosing edge lexicographically first and printing a cycle starting from that edge
    // is a way to make report deterministic and (as a result) to make testing those reports simple.
    int edgeIndex = chooseEdgeLexicographicallyFirst(cycle);
    var rotatedCycle = cycle.rotate(-edgeIndex);

    String previous = rotatedCycle.get(rotatedCycle.size() - 1).targetKey();
    var lines = new ArrayList<String>();
    for (var current : rotatedCycle) {
      String dependency = current.targetKey();
      lines.add(current.value() + ": " + previous + " ~> " + dependency);
      previous = dependency;
    }
    return error(name + " contains cycle:\n" + join("\n", lines));
  }

  private static int chooseEdgeLexicographicallyFirst(List<GraphEdge<Location, String>> cycle) {
    String firstLexicographically = cycle.get(0).value().toString();
    int result = 0;
    for (int i = 1; i < cycle.size(); i++) {
      String toString = cycle.get(i).value().toString();
      if (toString.compareTo(firstLexicographically) < 0) {
        firstLexicographically = toString;
        result = i;
      }
    }
    return result;
  }
}
