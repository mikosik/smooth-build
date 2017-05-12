package org.smoothbuild.parse.ast;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;

import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableList;

public class Ast {
  private final List<FunctionNode> functions;
  private Map<Name, FunctionNode> nameToFunctionMap;

  public static Ast ast(List<FunctionNode> functions) {
    return new Ast(functions);
  }

  private Ast(List<FunctionNode> functions) {
    this.functions = ImmutableList.copyOf(functions);
  }

  public List<FunctionNode> functions() {
    return functions;
  }

  public Map<Name, FunctionNode> nameToFunctionMap() {
    if (nameToFunctionMap == null) {
      nameToFunctionMap = createNameToFunctionMap();
    }
    return nameToFunctionMap;
  }

  private Map<Name, FunctionNode> createNameToFunctionMap() {
    return functions.stream()
        .collect(toImmutableMap(FunctionNode::name, identity(), (a, b) -> a));
  }
}
