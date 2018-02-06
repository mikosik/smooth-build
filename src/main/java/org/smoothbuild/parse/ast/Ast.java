package org.smoothbuild.parse.ast;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

import org.smoothbuild.lang.function.base.Name;

public class Ast {
  private final List<FuncNode> functions;
  private final Map<Name, FuncNode> nameToFunctionMap;

  public Ast(List<FuncNode> functions) {
    this.functions = functions;
    this.nameToFunctionMap = functions
        .stream()
        .collect(toMap(FuncNode::name, identity(), (a, b) -> a));
  }

  public List<FuncNode> functions() {
    return functions;
  }

  public boolean containsFunction(Name name) {
    return nameToFunctionMap.containsKey(name);
  }

  public FuncNode function(Name name) {
    if (!nameToFunctionMap.containsKey(name)) {
      throw new IllegalStateException("Ast does not contain function '" + name + "'");
    }
    return nameToFunctionMap.get(name);
  }
}
