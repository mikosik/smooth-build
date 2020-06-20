package org.smoothbuild.parse.ast;

import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.parse.ast.StructNode.typeNameToConstructorName;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.graph.SortTopologically.sortTopologically;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.parse.AstVisitor;
import org.smoothbuild.parse.Definitions;
import org.smoothbuild.util.graph.GraphEdge;
import org.smoothbuild.util.graph.GraphNode;
import org.smoothbuild.util.graph.SortTopologically.TopologicalSortingResult;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

public class Ast {
  private final ImmutableList<StructNode> structs;
  private final ImmutableList<FuncNode> funcs;
  private ImmutableMap<String, ParameterizedNode> functionsAndConstructorsMap;

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

  public ImmutableMap<String, ParameterizedNode> functionsAndConstructorsMap() {
    if (functionsAndConstructorsMap == null) {
      functionsAndConstructorsMap = createFunctionsAndConstructorsMap();
    }
    return functionsAndConstructorsMap;
  }

  private ImmutableMap<String, ParameterizedNode> createFunctionsAndConstructorsMap() {
    Builder<String, ParameterizedNode> builder = ImmutableMap.builder();
    for (StructNode struct : structs) {
      builder.put(typeNameToConstructorName(struct.name()), struct);
    }
    for (FuncNode func : funcs) {
      builder.put(func.name(), func);
    }
    return builder.build();
  }

  public Ast sortedByDependencies(Definitions definitions, Logger logger) {
    var sortedTypes = sortTypesByDependencies(definitions.types().keySet());
    if (sortedTypes.sorted() == null) {
      reportCycle(logger,"Type hierarchy" , sortedTypes.cycle());
      return null;
    }
    var sortedFunctions = sortFunctionsByDependencies(definitions.functions().keySet());
    if (sortedFunctions.sorted() == null) {
      reportCycle(logger, "Function call graph", sortedFunctions.cycle());
      return null;
    }
    return new Ast(sortedTypes.valuesReversed(), sortedFunctions.valuesReversed());
  }

  private TopologicalSortingResult<String, FuncNode, Location> sortFunctionsByDependencies(
      ImmutableSet<String> importedFunctionNames) {
    List<String> constructors = map(structs(), structNode -> structNode.constructor().name());
    var namesToSkip = ImmutableSet.<String>builder()
        .addAll(constructors)
        .addAll(importedFunctionNames)
        .build();
    var nodes = map(funcs(), func -> funcNodeToGraphNode(func, namesToSkip));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, FuncNode, Location> funcNodeToGraphNode(FuncNode func,
      ImmutableSet<String> importedFunctionNames) {
    Set<GraphEdge<Location, String>> dependencies = new HashSet<>();
    new AstVisitor() {
      @Override
      public void visitCall(CallNode call) {
        super.visitCall(call);
        if (!importedFunctionNames.contains(call.name())) {
          dependencies.add(new GraphEdge<>(call.location(), call.name()));
        }
      }
    }.visitFunc(func);
    return new GraphNode<>(func.name(), func, ImmutableList.copyOf(dependencies));
  }

  private TopologicalSortingResult<String, StructNode, Location> sortTypesByDependencies(
      ImmutableSet<String> importedTypeNames) {
    var nodes = map(structs(), struct -> structNodeToGraphNode(struct, importedTypeNames));
    return sortTopologically(nodes);
  }

  private static GraphNode<String, StructNode, Location> structNodeToGraphNode(
      StructNode structNode, ImmutableSet<String> importedTypeNames) {
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
          if (!importedTypeNames.contains(type.name())) {
            dependencies.add(new GraphEdge<>(type.location(), type.name()));
          }
        }
      }
    }.visitStruct(structNode);
    return new GraphNode<>(structNode.name(), structNode, ImmutableList.copyOf(dependencies));
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
