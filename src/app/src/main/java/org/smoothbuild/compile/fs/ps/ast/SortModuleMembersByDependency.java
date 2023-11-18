package org.smoothbuild.compile.fs.ps.ast;

import static java.lang.String.join;
import static java.util.Collections.rotate;
import static org.smoothbuild.common.collect.Lists.map;
import static org.smoothbuild.common.graph.SortTopologically.sortTopologically;
import static org.smoothbuild.out.log.Log.error;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.smoothbuild.common.collect.Sets;
import org.smoothbuild.common.graph.GraphEdge;
import org.smoothbuild.common.graph.GraphNode;
import org.smoothbuild.common.graph.SortTopologically.TopologicalSortingRes;
import org.smoothbuild.compile.fs.lang.base.Nal;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.ps.ast.define.ArrayTP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncTP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedEvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
import org.smoothbuild.compile.fs.ps.ast.define.TypeP;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;

import com.google.common.collect.ImmutableList;

/**
 * Sort module Evaluables and Structs based on dependencies between them.
 */
public class SortModuleMembersByDependency
    implements Function<ModuleP, Maybe<ModuleP>> {
  @Override
  public Maybe<ModuleP> apply(ModuleP moduleP) {
    var logBuffer = new LogBuffer();
    var sortedTs = sortStructsByDeps(moduleP.structs());
    if (sortedTs.sorted() == null) {
      logBuffer.log(createCycleError("Type hierarchy", sortedTs.cycle()));
      return Maybe.of(null, logBuffer);
    }
    var sortedEvaluables = sortEvaluablesByDeps(moduleP.evaluables());
    if (sortedEvaluables.sorted() == null) {
      logBuffer.log(createCycleError("Dependency graph", sortedEvaluables.cycle()));
      return Maybe.of(null, logBuffer);
    }
    ModuleP result = new ModuleP(
        moduleP.name(),
        sortedTs.valuesReversed(),
        sortedEvaluables.valuesReversed(),
        moduleP.scope());
    return Maybe.of(result, logBuffer);
  }

  private static TopologicalSortingRes<String, NamedEvaluableP, Location> sortEvaluablesByDeps(
      ImmutableList<NamedEvaluableP> evaluables) {
    HashSet<String> names = new HashSet<>();
    evaluables.forEach(r -> names.add(r.name()));

    HashSet<GraphNode<String, NamedEvaluableP, Location>> nodes = new HashSet<>();
    nodes.addAll(map(evaluables, r -> evaluable(r, names)));
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
    return new GraphNode<>(evaluable.name(), evaluable, ImmutableList.copyOf(deps));
  }

  private static TopologicalSortingRes<String, StructP, Location> sortStructsByDeps(
      List<StructP> structs) {
    var structNames = Sets.map(structs, Nal::name);
    var nodes = map(structs, struct -> structToGraphNode(struct, structNames));
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
    return new GraphNode<>(struct.name(), struct, ImmutableList.copyOf(deps));
  }

  private static Log createCycleError(String name, List<GraphEdge<Location, String>> cycle) {
    // Choosing edge lexicographically first and printing a cycle starting from that edge
    // is a way to make report deterministic and (as a result) to make testing those reports simple.
    int edgeIndex = chooseEdgeLexicographicallyFirst(cycle);
    rotate(cycle, -edgeIndex);

    String previous = cycle.get(cycle.size() - 1).targetKey();
    var lines = new ArrayList<String>();
    for (var current : cycle) {
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
