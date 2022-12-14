package org.smoothbuild.compile.ps.ast;

import static java.lang.String.join;
import static java.util.Collections.rotate;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.out.log.Maybe.maybeLogs;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.graph.SortTopologically.sortTopologically;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.compile.lang.base.Nal;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.ps.ast.expr.ItemP;
import org.smoothbuild.compile.ps.ast.expr.NamedEvaluableP;
import org.smoothbuild.compile.ps.ast.expr.RefP;
import org.smoothbuild.compile.ps.ast.expr.StructP;
import org.smoothbuild.compile.ps.ast.type.ArrayTP;
import org.smoothbuild.compile.ps.ast.type.FuncTP;
import org.smoothbuild.compile.ps.ast.type.TypeP;
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
    var sortedEvaluables = sortEvaluablesByDeps(ast.evaluables());
    if (sortedEvaluables.sorted() == null) {
      Log error = createCycleError("Dependency graph", sortedEvaluables.cycle());
      return maybeLogs(error);
    }
    Ast sorted = new Ast(sortedTs.valuesReversed(), sortedEvaluables.valuesReversed());
    return maybe(sorted);
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
    new AstVisitor() {
      @Override
      public void visitRef(RefP ref) {
        super.visitRef(ref);
        if (names.contains(ref.name())) {
          deps.add(new GraphEdge<>(ref.location(), ref.name()));
        }
      }
    }.visitNamedEvaluable(evaluable);
    return new GraphNode<>(evaluable.name(), evaluable, ImmutableList.copyOf(deps));
  }

  private static TopologicalSortingRes<String, StructP, Location> sortStructsByDeps(
      NList<StructP> structs) {
    var structNames = Sets.map(structs, Nal::name);
    var nodes = map(structs, struct -> structToGraphNode(struct, structNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, StructP, Location> structToGraphNode(
      StructP struct, Set<String> funcNames) {
    Set<GraphEdge<Location, String>> deps = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitField(ItemP field) {
        super.visitField(field);
        addToDeps(field.type());
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
              deps.add(new GraphEdge<>(type.location(), type.name()));
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
