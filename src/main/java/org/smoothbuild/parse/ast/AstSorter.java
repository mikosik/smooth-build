package org.smoothbuild.parse.ast;

import static java.lang.String.join;
import static java.util.Collections.rotate;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.out.log.Maybe.maybeValue;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.graph.SortTopologically.sortTopologically;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.collect.Sets;
import org.smoothbuild.util.graph.GraphEdge;
import org.smoothbuild.util.graph.GraphNode;
import org.smoothbuild.util.graph.SortTopologically.TopologicalSortingRes;

import com.google.common.collect.ImmutableList;

public class AstSorter {
  public static Maybe<Ast> sortParsedByDeps(Ast ast) {
    var sortedTs = sortStructsByDeps(ast.structs());
    if (sortedTs.sorted() == null) {
      Log error = createCycleError("Type hierarchy", sortedTs.cycle());
      return maybeLogs(error);
    }
    var sortedRefables = sortRefablesByDeps(ast.topRefables());
    if (sortedRefables.sorted() == null) {
      Log error = createCycleError("Dependency graph", sortedRefables.cycle());
      return maybeLogs(error);
    }
    Ast sorted = new Ast(sortedTs.valuesReversed(), sortedRefables.valuesReversed());
    return maybeValue(sorted);
  }

  private static TopologicalSortingRes<String, TopRefableP, Loc> sortRefablesByDeps(
      ImmutableList<TopRefableP> topRefables) {
    HashSet<String> names = new HashSet<>();
    topRefables.forEach(v -> names.add(v.name()));

    HashSet<GraphNode<String, TopRefableP, Loc>> nodes = new HashSet<>();
    nodes.addAll(map(topRefables, value -> refable(value, names)));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, TopRefableP, Loc> refable(TopRefableP refable,
      Set<String> names) {
    Set<GraphEdge<Loc, String>> deps = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitRef(RefP ref) {
        super.visitRef(ref);
        if (names.contains(ref.name())) {
          deps.add(new GraphEdge<>(ref.loc(), ref.name()));
        }
      }
    }.visitRefable(refable);
    return new GraphNode<>(refable.name(), refable, ImmutableList.copyOf(deps));
  }

  private static TopologicalSortingRes<String, StructP, Loc> sortStructsByDeps(
      NList<StructP> structs) {
    var structNames = Sets.map(structs, MonoNamedP::name);
    var nodes = map(structs, struct -> structToGraphNode(struct, structNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, StructP, Loc> structToGraphNode(
      StructP struct, Set<String> funcNames) {
    Set<GraphEdge<Loc, String>> deps = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitField(ItemP field) {
        super.visitField(field);
        addToDeps(field.typeP());
      }

      private void addToDeps(TypeP type) {
        switch (type) {
          case ArrayTP arrayT -> addToDeps(arrayT.elemT());
          case FuncTP funcT -> {
            addToDeps(funcT.resT());
            funcT.paramTs().forEach(this::addToDeps);
          }
          default -> {
            if (funcNames.contains(type.name())) {
              deps.add(new GraphEdge<>(type.loc(), type.name()));
            }
          }
        }
      }
    }.visitStruct(struct);
    return new GraphNode<>(struct.name(), struct, ImmutableList.copyOf(deps));
  }

  private static Log createCycleError(String name, List<GraphEdge<Loc, String>> cycle) {
    // Choosing edge with lowest line number and printing a cycle starting from that edge
    // is a way to make report deterministic and (as a result) to make testing those reports simple.
    int edgeIndex = chooseEdgeWithLowestLineNumber(cycle);
    rotate(cycle, -edgeIndex);

    String previous = cycle.get(cycle.size() - 1).targetKey();
    var lines = new ArrayList<String>();
    for (var current : cycle) {
      String dependency = current.targetKey();
      lines.add(current.value() + ": " + previous + " -> " + dependency);
      previous = dependency;
    }
    return error(name + " contains cycle:\n" + join("\n", lines));
  }

  private static int chooseEdgeWithLowestLineNumber(List<GraphEdge<Loc, String>> cycle) {
    int lowestLineNumber = Integer.MAX_VALUE;
    int result = 0;
    for (int i = 0; i < cycle.size(); i++) {
      int line = cycle.get(i).value().line();
      if (line < lowestLineNumber) {
        lowestLineNumber = line;
        result = i;
      }
    }
    return result;
  }

}
