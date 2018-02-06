package org.smoothbuild.parse.ast;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.parse.deps.SortByDependencies.sortByDependencies;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.util.Maybe;

public class Ast {
  private List<FuncNode> functions;
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

  public Maybe<Ast> sortFunctionsByDependencies(Functions globalFunctions) {
    Maybe<List<Name>> sortedNames = sortByDependencies(globalFunctions, this);
    if (sortedNames.hasValue()) {
      this.functions = sortedNames
          .value()
          .stream()
          .map(n -> nameToFunctionMap.get(n))
          .collect(Collectors.toList());
      return Maybe.value(this);
    } else {
      return Maybe.errors(sortedNames.errors());
    }
  }
}
