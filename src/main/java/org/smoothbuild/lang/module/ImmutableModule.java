package org.smoothbuild.lang.module;

import java.util.Map;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ImmutableModule implements Module {
  private final ImmutableMap<Name, Function> functions;

  public ImmutableModule(Map<Name, ? extends Function> definedFunctions) {
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
