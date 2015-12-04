package org.smoothbuild.lang.module;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.lang.function.base.Function;
import org.smoothbuild.lang.function.base.Name;

public class ModuleBuilder {
  private final Map<Name, Function> functions;

  @Inject
  public ModuleBuilder() {
    this.functions = new HashMap<>();
  }

  public void addFunction(Function function) {
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
