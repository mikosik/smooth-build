package org.smoothbuild.lang.parse.ast;

import static java.lang.String.join;
import static java.util.Collections.rotate;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.cli.console.ImmutableLogs.logs;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValue;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nListWithNonUniqueNames;
import static org.smoothbuild.util.graph.SortTopologically.sortTopologically;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.graph.GraphEdge;
import org.smoothbuild.util.graph.GraphNode;
import org.smoothbuild.util.graph.SortTopologically.TopologicalSortingRes;

import com.google.common.collect.ImmutableList;

public class Ast {
  private final NList<StructN> structs;
  private final ImmutableList<EvalN> topEvals;

  public Ast(List<StructN> structs, List<EvalN> topEvals) {
    this.structs = nListWithNonUniqueNames(ImmutableList.copyOf(structs));
    this.topEvals = ImmutableList.copyOf(topEvals);
  }

  public ImmutableList<EvalN> topEvals() {
    return topEvals;
  }

  public NList<StructN> structs() {
    return structs;
  }

  public Maybe<Ast> sortedByDeps() {
    var sortedTypes = sortStructsByDeps();
    if (sortedTypes.sorted() == null) {
      Log error = createCycleError("Type hierarchy", sortedTypes.cycle());
      return maybeLogs(logs(error));
    }
    var sortedEvals = sortEvalsByDeps();
    if (sortedEvals.sorted() == null) {
      Log error = createCycleError("Dependency graph", sortedEvals.cycle());
      return maybeLogs(logs(error));
    }
    Ast ast = new Ast(sortedTypes.valuesReversed(), sortedEvals.valuesReversed());
    return maybeValue(ast);
  }

  private TopologicalSortingRes<String, EvalN, Loc> sortEvalsByDeps() {
    HashSet<String> names = new HashSet<>();
    topEvals.forEach(v -> names.add(v.name()));

    HashSet<GraphNode<String, EvalN, Loc>> nodes = new HashSet<>();
    nodes.addAll(map(topEvals, value -> evalToGraphNode(value, names)));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, EvalN, Loc> evalToGraphNode(EvalN evaluable, Set<String> names) {
    Set<GraphEdge<Loc, String>> deps = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitRef(RefN ref) {
        super.visitRef(ref);
        if (names.contains(ref.name())) {
          deps.add(new GraphEdge<>(ref.loc(), ref.name()));
        }
      }
    }.visitEvaluable(evaluable);
    return new GraphNode<>(evaluable.name(), evaluable, ImmutableList.copyOf(deps));
  }

  private TopologicalSortingRes<String, StructN, Loc> sortStructsByDeps() {
    Set<String> structNames = structs.stream()
        .map(NamedN::name)
        .collect(toSet());
    var nodes = map(structs, struct -> structToGraphNode(struct, structNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, StructN, Loc> structToGraphNode(
      StructN struct, Set<String> funcNames) {
    Set<GraphEdge<Loc, String>> deps = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitField(ItemN field) {
        super.visitField(field);
        field.typeNode().ifPresent(this::addToDeps);
      }

      private void addToDeps(TypeN type) {
        switch (type) {
          case ArrayTN arrayType -> addToDeps(arrayType.elemType());
          case FuncTN funcType -> {
            addToDeps(funcType.resType());
            funcType.paramTypes().forEach(this::addToDeps);
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
