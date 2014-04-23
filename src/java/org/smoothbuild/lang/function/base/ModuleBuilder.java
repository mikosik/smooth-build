package org.smoothbuild.lang.function.base;

import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.lang.function.nativ.NativeFunction;

import com.google.common.collect.Maps;

public class ModuleBuilder {
  private final Map<Name, Function<?>> functions;

  @Inject
  public ModuleBuilder() {
    this.functions = Maps.newHashMap();
  }

  public void addFunction(NativeFunction<?> function) {
    Name name = function.name();
    if (functions.containsKey(name)) {
      throw new IllegalArgumentException("Function " + name
          + " has been already added to this module.");
    } else {
      functions.put(name, function);
    }
  }

  public Module build() {
    return new ImmutableModule(functions);
  }
}
