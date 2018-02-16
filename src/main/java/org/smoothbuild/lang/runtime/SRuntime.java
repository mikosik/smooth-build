package org.smoothbuild.lang.runtime;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.lang.function.Functions;
import org.smoothbuild.lang.type.RuntimeTypes;

@Singleton
public class SRuntime {
  private final RuntimeTypes types;
  private final Functions functions;

  @Inject
  public SRuntime(RuntimeTypes types, Functions functions) {
    this.types = types;
    this.functions = functions;
  }

  public RuntimeTypes types() {
    return types;
  }

  public Functions functions() {
    return functions;
  }
}
