package org.smoothbuild.function.base;

import java.util.Map;

import org.smoothbuild.function.def.DefinedFunction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class Module {
  private final ImmutableMap<QualifiedName, DefinedFunction> functions;

  public Module(Map<QualifiedName, DefinedFunction> definedFunctions) {
    this.functions = ImmutableMap.copyOf(definedFunctions);
  }

  public Function getFunction(QualifiedName name) {
    return functions.get(name);
  }

  public ImmutableSet<QualifiedName> availableNames() {
    return functions.keySet();
  }
}
