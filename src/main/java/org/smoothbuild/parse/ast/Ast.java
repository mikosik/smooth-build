package org.smoothbuild.parse.ast;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.parse.deps.SortByDependencies.sortByDependencies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.util.Maybe;

public class Ast {
  private List<FuncNode> funcs;
  private final Map<String, FuncNode> nameToFunsMap;

  public Ast(List<FuncNode> funcs) {
    this.funcs = funcs;
    this.nameToFunsMap = funcs
        .stream()
        .collect(toMap(FuncNode::name, identity(), (a, b) -> a));
  }

  public List<FuncNode> funcs() {
    return funcs;
  }

  public boolean containsFunc(String name) {
    return nameToFunsMap.containsKey(name);
  }

  public FuncNode func(String name) {
    if (!nameToFunsMap.containsKey(name)) {
      throw new IllegalStateException("Ast does not contain function '" + name + "'");
    }
    return nameToFunsMap.get(name);
  }

  public List<Object> sortFuncsByDependencies(Functions functions) {
    Maybe<List<String>> sortedNames = sortByDependencies(functions, this);
    if (sortedNames.hasValue()) {
      this.funcs = sortedNames
          .value()
          .stream()
          .map(n -> nameToFunsMap.get(n))
          .collect(Collectors.toList());
      return new ArrayList<>();
    } else {
      return sortedNames.errors();
    }
  }
}
