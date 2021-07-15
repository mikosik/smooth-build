package org.smoothbuild.lang.parse.ast;

import static java.lang.String.join;
import static java.util.Collections.rotate;
import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.cli.console.ImmutableLogs.logs;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Maybe.maybeLogs;
import static org.smoothbuild.cli.console.Maybe.maybeValue;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.graph.SortTopologically.sortTopologically;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.cli.console.Log;
import org.smoothbuild.cli.console.Maybe;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.graph.GraphEdge;
import org.smoothbuild.util.graph.GraphNode;
import org.smoothbuild.util.graph.SortTopologically.TopologicalSortingResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Ast {
  private final ImmutableList<StructNode> structs;
  private final ImmutableList<ReferencableNode> referencables;
  private ImmutableMap<String, ReferencableNode> referencablesMap;
  private ImmutableMap<String, StructNode> structsMap;

  public Ast(List<StructNode> structs, List<ReferencableNode> referencables) {
    this.structs = ImmutableList.copyOf(structs);
    this.referencables = ImmutableList.copyOf(referencables);
  }

  public ImmutableList<ReferencableNode> referencable() {
    return referencables;
  }

  public ImmutableList<StructNode> structs() {
    return structs;
  }

  public ImmutableMap<String, ReferencableNode> referencablesMap() {
    if (referencablesMap == null) {
      referencablesMap = createReferencablesMap();
    }
    return referencablesMap;
  }

  private ImmutableMap<String, ReferencableNode> createReferencablesMap() {
    var result = new HashMap<String, ReferencableNode>();
    new AstVisitor() {
      @Override
      public void visitReferencable(ReferencableNode referencable) {
        super.visitReferencable(referencable);
        if (!result.containsKey(referencable.name())) {
          result.put(referencable.name(), referencable);
        }
      }
    }.visitAst(this);
    return ImmutableMap.copyOf(result);
  }

  public ImmutableMap<String, StructNode> structsMap() {
    if (structsMap == null) {
      structsMap = createStructsMap();
    }
    return structsMap;
  }

  private ImmutableMap<String, StructNode> createStructsMap() {
    var builder = ImmutableMap.<String, StructNode>builder();
    new AstVisitor() {
      @Override
      public void visitStruct(StructNode struct) {
        builder.put(struct.name(), struct);
      }
    }.visitAst(this);
    return builder.build();
  }

  public Maybe<Ast> sortedByDependencies() {
    var sortedTypes = sortStructsByDependencies();
    if (sortedTypes.sorted() == null) {
      Log error = createCycleError("Type hierarchy", sortedTypes.cycle());
      return maybeLogs(logs(error));
    }
    var sortedReferencables = sortReferencablesByDependencies();
    if (sortedReferencables.sorted() == null) {
      Log error = createCycleError("Dependency graph", sortedReferencables.cycle());
      return maybeLogs(logs(error));
    }
    Ast ast = new Ast(sortedTypes.valuesReversed(), sortedReferencables.valuesReversed());
    return maybeValue(ast);
  }

  private TopologicalSortingResult<String, ReferencableNode, Location>
      sortReferencablesByDependencies() {
    HashSet<String> names = new HashSet<>();
    referencables.forEach(v -> names.add(v.name()));

    HashSet<GraphNode<String, ReferencableNode, Location>> nodes = new HashSet<>();
    nodes.addAll(map(referencables, value -> referencableNodeToGraphNode(value, names)));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, ReferencableNode, Location> referencableNodeToGraphNode(
      ReferencableNode referencable, Set<String> names) {
    Set<GraphEdge<Location, String>> dependencies = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitRef(RefNode ref) {
        super.visitRef(ref);
        if (names.contains(ref.name())) {
          dependencies.add(new GraphEdge<>(ref.location(), ref.name()));
        }
      }
    }.visitReferencable(referencable);
    return new GraphNode<>(referencable.name(), referencable, ImmutableList.copyOf(dependencies));
  }

  private TopologicalSortingResult<String, StructNode, Location> sortStructsByDependencies() {
    Set<String> structNames = structs.stream()
        .map(NamedNode::name)
        .collect(toSet());
    var nodes = map(structs, struct -> structNodeToGraphNode(struct, structNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, StructNode, Location> structNodeToGraphNode(
      StructNode struct, Set<String> funcNames) {
    Set<GraphEdge<Location, String>> dependencies = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitField(ItemNode field) {
        super.visitField(field);
        field.typeNode().ifPresent(this::addToDependencies);
      }

      private void addToDependencies(TypeNode type) {
        if (type instanceof ArrayTypeNode arrayType) {
          addToDependencies(arrayType.elementType());
        } else if (type instanceof FunctionTypeNode functionType) {
          addToDependencies(functionType.resultType());
          functionType.parameterTypes().forEach(this::addToDependencies);
        } else {
          if (funcNames.contains(type.name())) {
            dependencies.add(new GraphEdge<>(type.location(), type.name()));
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
