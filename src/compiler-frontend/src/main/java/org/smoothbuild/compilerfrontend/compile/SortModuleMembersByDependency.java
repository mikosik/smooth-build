package org.smoothbuild.compilerfrontend.compile;

import static java.lang.String.join;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.graph.SortTopologically.sortTopologically;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILER_FRONT_LABEL;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import java.util.ArrayList;
import java.util.HashSet;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Set;
import org.smoothbuild.common.graph.GraphEdge;
import org.smoothbuild.common.graph.GraphNode;
import org.smoothbuild.common.graph.SortTopologically.TopologicalSortingRes;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.compilerfrontend.compile.ast.PModuleVisitor;
import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.name.Id;

/**
 * Sort module Evaluables and Structs based on dependencies between them.
 */
public class SortModuleMembersByDependency implements Task1<PModule, PModule> {
  @Override
  public Output<PModule> execute(PModule pModule) {
    var label = COMPILER_FRONT_LABEL.append(":sortMembers");
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
        pModule.fileName(),
        sortedTs.valuesReversed(),
        sortedEvaluables.valuesReversed(),
        pModule.scope());
    return output(result, label, list());
  }

  private static TopologicalSortingRes<Id, PNamedEvaluable, Location> sortEvaluablesByDeps(
      List<PNamedEvaluable> evaluables) {
    HashSet<Id> ids = new HashSet<>();
    evaluables.forEach(e -> ids.add(e.id()));
    return sortTopologically(evaluables.map(e -> evaluable(e, ids)));
  }

  private static GraphNode<Id, PNamedEvaluable, Location> evaluable(
      PNamedEvaluable evaluable, HashSet<Id> ids) {
    HashSet<GraphEdge<Location, Id>> deps = new HashSet<>();
    new PModuleVisitor<RuntimeException>() {
      @Override
      public void visitReference(PReference pReference) {
        super.visitReference(pReference);
        if (ids.contains(pReference.id())) {
          deps.add(new GraphEdge<>(pReference.location(), pReference.id()));
        }
      }

      @Override
      public void visitItem(PItem pItem) {
        super.visitItem(pItem);
        pItem.defaultValueId().ifPresent(id -> deps.add(new GraphEdge<>(pItem.location(), id)));
      }
    }.visitNamedEvaluable(evaluable);
    return new GraphNode<>(evaluable.id(), evaluable, listOfAll(deps));
  }

  private static TopologicalSortingRes<Id, PStruct, Location> sortStructsByDeps(
      List<PStruct> structs) {
    var structNames = structs.map(pStruct -> pStruct.id().toString()).toSet();
    var nodes = structs.map(struct -> structToGraphNode(struct, structNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<Id, PStruct, Location> structToGraphNode(
      PStruct struct, Set<String> structNames) {
    HashSet<GraphEdge<Location, Id>> deps = new HashSet<>();
    new PModuleVisitor<RuntimeException>() {
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
            if (structNames.contains(pType.nameText())) {
              deps.add(new GraphEdge<>(pType.location(), fqn(pType.nameText())));
            }
          }
        }
      }
    }.visitStruct(struct);
    return new GraphNode<>(struct.id(), struct, listOfAll(deps));
  }

  private static Log createCycleError(String name, List<GraphEdge<Location, Id>> cycle) {
    // Choosing edge lexicographically first and printing a cycle starting from that edge
    // is a way to make report deterministic and (as a result) to make testing those reports simple.
    int edgeIndex = chooseEdgeLexicographicallyFirst(cycle);
    var rotatedCycle = cycle.rotate(-edgeIndex);

    var previous = rotatedCycle.get(rotatedCycle.size() - 1).targetKey();
    var lines = new ArrayList<String>();
    for (var current : rotatedCycle) {
      var dependency = current.targetKey();
      lines.add(current.value() + ": " + previous + " ~> " + dependency);
      previous = dependency;
    }
    return error(name + " contains cycle:\n" + join("\n", lines));
  }

  private static int chooseEdgeLexicographicallyFirst(List<GraphEdge<Location, Id>> cycle) {
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
