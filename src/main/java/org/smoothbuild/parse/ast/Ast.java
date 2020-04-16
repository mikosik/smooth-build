package org.smoothbuild.parse.ast;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.smoothbuild.parse.deps.SortByDependencies.sortByDependencies;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.cli.console.LoggerImpl;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.runtime.Functions;

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

  public void sortFuncsByDependencies(Functions functions, Logger logger) {
    List<String> sortedNames = sortByDependencies(functions, this, logger);
    if (sortedNames != null) {
      this.funcs = sortedNames
          .stream()
          .map(nameToFuncMap::get)
          .collect(Collectors.toList());
    }
  }

  public void sortTypesByDependencies(ObjectFactory objectFactory, LoggerImpl logger) {
    List<String> sortedNames = sortByDependencies(objectFactory, this, logger);
    if (sortedNames != null) {
      this.structs = sortedNames
          .stream()
          .map(nameToStructMap::get)
          .collect(Collectors.toList());
    }
  }
}
