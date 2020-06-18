package org.smoothbuild.parse.ast;

import static org.smoothbuild.parse.ast.StructNode.typeNameToConstructorName;
import static org.smoothbuild.parse.deps.SortByDependencies.sortFunctionsByDependencies;
import static org.smoothbuild.parse.deps.SortByDependencies.sortTypesByDependencies;

import java.util.List;

import org.smoothbuild.cli.console.Logger;
import org.smoothbuild.parse.Defined;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class Ast {
  private final ImmutableList<StructNode> structs;
  private final ImmutableList<FuncNode> funcs;

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

  public ImmutableMap<String, ParameterizedNode> createFunctionsAndConstructorsMap() {
    Builder<String, ParameterizedNode> builder = ImmutableMap.builder();
    for (StructNode struct : structs) {
      builder.put(typeNameToConstructorName(struct.name()), struct);
    }
    for (FuncNode func : funcs) {
      builder.put(func.name(), func);
    }
    return builder.build();
  }

  public Ast sortedByDependencies(Defined defined, Logger logger) {
    return new Ast(
        sortTypesByDependencies(defined.types(), this, logger),
        sortFunctionsByDependencies(defined.functions(), this, logger));
  }
}
