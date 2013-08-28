package org.smoothbuild.function.base;

import java.util.Map;

import org.smoothbuild.function.def.DefinedFunction;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class Module {
  private final ImmutableMap<Name, DefinedFunction> functions;

  public Module(Map<Name, DefinedFunction> definedFunctions) {
    this.functions = ImmutableMap.copyOf(definedFunctions);
  }

  public Function getFunction(Name name) {
    return functions.get(name);
  }

  public ImmutableSet<Name> availableNames() {
    return functions.keySet();
  }
}
