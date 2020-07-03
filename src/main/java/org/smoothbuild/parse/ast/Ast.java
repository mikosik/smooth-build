package org.smoothbuild.parse.ast;

import static java.util.stream.Collectors.toSet;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.graph.SortTopologically.sortTopologically;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.util.graph.GraphEdge;
import org.smoothbuild.util.graph.GraphNode;
import org.smoothbuild.util.graph.SortTopologically.TopologicalSortingResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Ast {
  private final ImmutableList<StructNode> structs;
  private final ImmutableList<FuncNode> funcs;
  private ImmutableMap<String, CallableNode> callablesMap;

  public Ast(List<StructNode> structs, List<FuncNode> funcs) {
    this.structs = ImmutableList.copyOf(structs);
    this.funcs = ImmutableList.copyOf(funcs);
  }

  public List<FuncNode> funcs() {
    return funcs;
  }

  public List<StructNode> structs() {
    return structs;
  }

  public ImmutableMap<String, CallableNode> callablesMap() {
    if (callablesMap == null) {
      callablesMap = createCallablesMap();
    }
    return callablesMap;
  }

  private ImmutableMap<String, CallableNode> createCallablesMap() {
    Builder<String, CallableNode> builder = ImmutableMap.builder();
    structs.stream()
        .map(StructNode::constructor)
        .forEach(c -> builder.put(c.name(), c));
    funcs.forEach(f -> builder.put(f.name(), f));
    return builder.build();
  }

  public Ast sortedByDependencies(Logger logger) {
    var sortedTypes = sortTypesByDependencies();
    if (sortedTypes.sorted() == null) {
      reportCycle(logger,"Type hierarchy" , sortedTypes.cycle());
      return null;
    }
    var sortedFunctions = sortFunctionsByDependencies();
    if (sortedFunctions.sorted() == null) {
      reportCycle(logger, "Function call graph", sortedFunctions.cycle());
      return null;
    }
    return new Ast(sortedTypes.valuesReversed(), sortedFunctions.valuesReversed());
  }

  private TopologicalSortingResult<String, FuncNode, Location> sortFunctionsByDependencies() {
    Set<String> funcNames = funcs.stream()
        .map(NamedNode::name)
        .collect(toSet());
    var nodes = map(funcs(), func -> funcNodeToGraphNode(func, funcNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, FuncNode, Location> funcNodeToGraphNode(FuncNode func,
      Set<String> funcNames) {
    Set<GraphEdge<Location, String>> dependencies = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        if (funcNames.contains(call.name())) {
          dependencies.add(new GraphEdge<>(call.location(), call.name()));
        }
      }
    }.visitFunc(func);
    return new GraphNode<>(func.name(), func, ImmutableList.copyOf(dependencies));
  }

  private TopologicalSortingResult<String, StructNode, Location> sortTypesByDependencies() {
    Set<String> structNames = structs.stream()
        .map(NamedNode::name)
        .collect(toSet());
    var nodes = map(structs(), struct -> structNodeToGraphNode(struct, structNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, StructNode, Location> structNodeToGraphNode(
      StructNode struct, Set<String> funcNames) {
    Set<GraphEdge<Location, String>> dependencies = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitField(int index, FieldNode field) {
        super.visitField(index, field);
        addToDependencies(field.typeNode());
      }

      private void addToDependencies(TypeNode type) {
        if (type.isArray()) {
          addToDependencies(((ArrayTypeNode) type).elementType());
        } else {
          if (funcNames.contains(type.name())) {
            dependencies.add(new GraphEdge<>(type.location(), type.name()));
          }
        }
      }
    }.visitStruct(struct);
    return new GraphNode<>(struct.name(), struct, ImmutableList.copyOf(dependencies));
  }

  private static void reportCycle(Logger logger, String name,
      List<GraphEdge<Location, String>> cycle) {
    StringBuilder builder = new StringBuilder();
    String previous = cycle.get(cycle.size() - 1).targetKey();
    for (var current : cycle) {
      Location location = current.value();
      String dependency = current.targetKey();
      builder.append(location);
      builder.append(": ");
      builder.append(previous);
      builder.append(" -> ");
      builder.append(dependency);
      builder.append("\n");
      previous = dependency;
    }
    logger.log(error(name + " contains cycle:\n" + builder.toString()));
  }
}
