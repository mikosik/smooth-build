package org.smoothbuild.lang.parse.ast;

import static java.lang.String.join;
import static java.util.Collections.rotate;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.cli.console.ImmutableLogs.logs;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValue;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nListWithDuplicates;
import static org.smoothbuild.util.graph.SortTopologically.sortTopologically;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.collect.NList;
import org.smoothbuild.util.graph.GraphEdge;
import org.smoothbuild.util.graph.GraphNode;
import org.smoothbuild.util.graph.SortTopologically.TopologicalSortingResult;

import com.google.common.collect.ImmutableList;

public class Ast {
  private final NList<StructN> structs;
  private final ImmutableList<EvaluableN> evaluables;

  public Ast(List<StructN> structs, List<EvaluableN> evaluables) {
    this.structs = nListWithDuplicates(ImmutableList.copyOf(structs));
    this.evaluables = ImmutableList.copyOf(evaluables);
  }

  public ImmutableList<EvaluableN> evaluables() {
    return evaluables;
  }

  public NList<StructN> structs() {
    return structs;
  }

  public Maybe<Ast> sortedByDependencies() {
    var sortedTypes = sortStructsByDependencies();
    if (sortedTypes.sorted() == null) {
      Log error = createCycleError("Type hierarchy", sortedTypes.cycle());
      return maybeLogs(logs(error));
    }
    var sortedReferencables = sortEvaluablesByDependencies();
    if (sortedReferencables.sorted() == null) {
      Log error = createCycleError("Dependency graph", sortedReferencables.cycle());
      return maybeLogs(logs(error));
    }
    Ast ast = new Ast(sortedTypes.valuesReversed(), sortedReferencables.valuesReversed());
    return maybeValue(ast);
  }

  private TopologicalSortingResult<String, EvaluableN, Location> sortEvaluablesByDependencies() {
    HashSet<String> names = new HashSet<>();
    evaluables.forEach(v -> names.add(v.name()));

    HashSet<GraphNode<String, EvaluableN, Location>> nodes = new HashSet<>();
    nodes.addAll(map(evaluables, value -> evaluableNodeToGraphNode(value, names)));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, EvaluableN, Location> evaluableNodeToGraphNode(
      EvaluableN evaluable, Set<String> names) {
    Set<GraphEdge<Location, String>> dependencies = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitRef(RefN ref) {
        super.visitRef(ref);
        if (names.contains(ref.name())) {
          dependencies.add(new GraphEdge<>(ref.location(), ref.name()));
        }
      }
    }.visitEvaluable(evaluable);
    return new GraphNode<>(evaluable.name(), evaluable, ImmutableList.copyOf(dependencies));
  }

  private TopologicalSortingResult<String, StructN, Location> sortStructsByDependencies() {
    Set<String> structNames = structs.stream()
        .map(NamedN::name)
        .collect(toSet());
    var nodes = map(structs, struct -> structNodeToGraphNode(struct, structNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, StructN, Location> structNodeToGraphNode(
      StructN struct, Set<String> funcNames) {
    Set<GraphEdge<Location, String>> dependencies = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitField(ItemN field) {
        super.visitField(field);
        field.typeNode().ifPresent(this::addToDependencies);
      }

      private void addToDependencies(TypeN type) {
        switch (type) {
          case ArrayTypeN arrayType -> addToDependencies(arrayType.elemType());
          case FunctionTypeN functionType -> {
            addToDependencies(functionType.resultType());
            functionType.paramTypes().forEach(this::addToDependencies);
          }
          default -> {
            if (funcNames.contains(type.name())) {
              dependencies.add(new GraphEdge<>(type.location(), type.name()));
            }
          }
        }
      }
    }.visitStruct(struct);
    return new GraphNode<>(struct.name(), struct, ImmutableList.copyOf(dependencies));
  }

  private static Log createCycleError(String name, List<GraphEdge<Location, String>> cycle) {
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

  private static int chooseEdgeWithLowestLineNumber(List<GraphEdge<Location, String>> cycle) {
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
