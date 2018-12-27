package org.smoothbuild.parse.ast;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.parse.deps.SortByDependencies.sortByDependencies;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.smoothbuild.lang.runtime.Functions;
import org.smoothbuild.lang.runtime.RuntimeTypes;
import org.smoothbuild.util.Maybe;

public class Ast {
  private List<StructNode> structs;
  private List<FuncNode> funcs;
  private final Map<String, StructNode> nameToStructMap;
  private final Map<String, FuncNode> nameToFuncMap;

  public Ast(List<StructNode> structs, List<FuncNode> funcs) {
    this.structs = structs;
    this.nameToStructMap = structs
        .stream()
        .collect(toMap(StructNode::name, identity(), (a, b) -> a));
    this.funcs = funcs;
    this.nameToFuncMap = funcs
        .stream()
        .collect(toMap(FuncNode::name, identity(), (a, b) -> a));
  }

  public List<FuncNode> funcs() {
    return funcs;
  }

  public boolean containsFunc(String name) {
    return nameToFuncMap.containsKey(name);
  }

  public FuncNode func(String name) {
    if (!nameToFuncMap.containsKey(name)) {
      throw new IllegalStateException("Ast does not contain function '" + name + "'");
    }
    return nameToFuncMap.get(name);
  }

  public List<StructNode> structs() {
    return structs;
  }

  public boolean containsStruct(String name) {
    return nameToStructMap.containsKey(name);
  }

  public StructNode struct(String name) {
    if (!nameToStructMap.containsKey(name)) {
      throw new IllegalStateException("Ast does not contain struct '" + name + "'");
    }
    return nameToStructMap.get(name);
  }

  public List<Object> sortFuncsByDependencies(Functions functions) {
    Maybe<List<String>> sortedNames = sortByDependencies(functions, this);
    if (sortedNames.hasValue()) {
      this.funcs = sortedNames
          .value()
          .stream()
          .map(nameToFuncMap::get)
          .collect(Collectors.toList());
      return new ArrayList<>();
    } else {
      return sortedNames.errors();
    }
  }

  public List<Object> sortTypesByDependencies(RuntimeTypes types) {
    Maybe<List<String>> sortedNames = sortByDependencies(types, this);
    if (sortedNames.hasValue()) {
      this.structs = sortedNames
          .value()
          .stream()
          .map(nameToStructMap::get)
          .collect(Collectors.toList());
      return new ArrayList<>();
    } else {
      return sortedNames.errors();
    }
  }
}
