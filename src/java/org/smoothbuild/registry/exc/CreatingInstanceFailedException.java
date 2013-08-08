package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.Function;

@SuppressWarnings("serial")
public class CreatingInstanceFailedException extends Exception {
  private final Class<? extends Function> klass;

  public CreatingInstanceFailedException(Class<? extends Function> klass, Throwable e) {
    super(e);
    this.klass = klass;
  }

  public Class<? extends Function> classThatFailed() {
    return klass;
  }
}
