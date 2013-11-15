package org.smoothbuild.lang.function.base;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ImmutableModule implements Module {
  private final ImmutableMap<Name, Function> functions;

  public ImmutableModule(Map<Name, Function> definedFunctions) {
    this.functions = ImmutableMap.copyOf(definedFunctions);
  }

  @Override
  public boolean containsFunction(Name name) {
    return functions.containsKey(name);
  }

  @Override
  public Function getFunction(Name name) {
    return functions.get(name);
  }

  @Override
  public ImmutableSet<Name> availableNames() {
    return functions.keySet();
  }
}
