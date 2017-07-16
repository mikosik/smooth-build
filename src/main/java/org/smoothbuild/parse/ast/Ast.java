package org.smoothbuild.parse.ast;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;

import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableList;

public class Ast {
  private final List<FuncNode> functions;
  private Map<Name, FuncNode> nameToFunctionMap;

  public Ast(List<FuncNode> functions) {
    this.functions = ImmutableList.copyOf(functions);
  }

  public List<FuncNode> functions() {
    return functions;
  }

  public Map<Name, FuncNode> nameToFunctionMap() {
    if (nameToFunctionMap == null) {
      nameToFunctionMap = createNameToFunctionMap();
    }
    return nameToFunctionMap;
  }

  private Map<Name, FuncNode> createNameToFunctionMap() {
    return functions.stream()
        .collect(toImmutableMap(FuncNode::name, identity(), (a, b) -> a));
  }
}
