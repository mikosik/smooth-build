package org.smoothbuild.compile.frontend.compile.ast;

import static java.lang.String.join;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.graph.SortTopologically.sortTopologically;
import static org.smoothbuild.common.log.Log.error;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.graph.GraphEdge;
import org.smoothbuild.common.graph.GraphNode;
import org.smoothbuild.common.graph.SortTopologically.TopologicalSortingRes;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.Logger;
import org.smoothbuild.common.log.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.compile.frontend.compile.ast.define.ArrayTP;
import org.smoothbuild.compile.frontend.compile.ast.define.FuncTP;
import org.smoothbuild.compile.frontend.compile.ast.define.ModuleP;
import org.smoothbuild.compile.frontend.compile.ast.define.NamedEvaluableP;
import org.smoothbuild.compile.frontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compile.frontend.compile.ast.define.StructP;
import org.smoothbuild.compile.frontend.compile.ast.define.TypeP;
import org.smoothbuild.compile.frontend.lang.base.Nal;
import org.smoothbuild.compile.frontend.lang.base.location.Location;

/**
 * Sort module Evaluables and Structs based on dependencies between them.
 */
public class SortModuleMembersByDependency implements TryFunction<ModuleP, ModuleP> {
  @Override
  public Try<ModuleP> apply(ModuleP moduleP) {
    var logger = new Logger();
    var sortedTs = sortStructsByDeps(moduleP.structs());
    if (sortedTs.sorted() == null) {
      logger.log(createCycleError("Type hierarchy", sortedTs.cycle()));
      return Try.of(null, logger);
    }
    var sortedEvaluables = sortEvaluablesByDeps(moduleP.evaluables());
    if (sortedEvaluables.sorted() == null) {
      logger.log(createCycleError("Dependency graph", sortedEvaluables.cycle()));
      return Try.of(null, logger);
    }
    ModuleP result = new ModuleP(
        moduleP.name(),
        sortedTs.valuesReversed(),
        sortedEvaluables.valuesReversed(),
        moduleP.scope());
    return Try.of(result, logger);
  }

  private static TopologicalSortingRes<String, NamedEvaluableP, Location> sortEvaluablesByDeps(
      List<NamedEvaluableP> evaluables) {
    HashSet<String> names = new HashSet<>();
    evaluables.forEach(r -> names.add(r.name()));

    HashSet<GraphNode<String, NamedEvaluableP, Location>> nodes = new HashSet<>();
    nodes.addAll(evaluables.map(r -> evaluable(r, names)));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, NamedEvaluableP, Location> evaluable(
      NamedEvaluableP evaluable, Set<String> names) {
    Set<GraphEdge<Location, String>> deps = new HashSet<>();
    new ModuleVisitorP() {
      @Override
      public void visitReference(ReferenceP referenceP) {
        super.visitReference(referenceP);
        if (names.contains(referenceP.name())) {
          deps.add(new GraphEdge<>(referenceP.location(), referenceP.name()));
        }
      }
    }.visitNamedEvaluable(evaluable);
    return new GraphNode<>(evaluable.name(), evaluable, listOfAll(deps));
  }

  private static TopologicalSortingRes<String, StructP, Location> sortStructsByDeps(
      List<StructP> structs) {
    var structNames = structs.map(Nal::name).toSet();
    var nodes = structs.map(struct -> structToGraphNode(struct, structNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, StructP, Location> structToGraphNode(
      StructP struct, Set<String> funcNames) {
    Set<GraphEdge<Location, String>> deps = new HashSet<>();
    new ModuleVisitorP() {
      @Override
      public void visitStructSignature(StructP structP) {
        super.visitStructSignature(structP);
        structP.fields().forEach(f -> addToDeps(f.type()));
      }

      private void addToDeps(TypeP typeP) {
        switch (typeP) {
          case ArrayTP arrayT -> addToDeps(arrayT.elemT());
          case FuncTP funcT -> {
            addToDeps(funcT.result());
            funcT.params().forEach(this::addToDeps);
          }
          default -> {
            if (funcNames.contains(typeP.name())) {
              deps.add(new GraphEdge<>(typeP.location(), typeP.name()));
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
