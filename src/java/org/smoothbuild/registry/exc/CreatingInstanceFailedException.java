package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.FunctionDefinition;

@SuppressWarnings("serial")
public class CreatingInstanceFailedException extends Exception {
  private final Class<? extends FunctionDefinition> klass;

  public CreatingInstanceFailedException(Class<? extends FunctionDefinition> klass, Throwable e) {
    super(e);
    this.klass = klass;
  }

  public Class<? extends FunctionDefinition> classThatFailed() {
    return klass;
  }
}
