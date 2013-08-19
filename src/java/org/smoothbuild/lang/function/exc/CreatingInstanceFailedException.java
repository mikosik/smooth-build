package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.function.FunctionDefinition;

@SuppressWarnings("serial")
public class CreatingInstanceFailedException extends FunctionException {
  private final Class<? extends FunctionDefinition> klass;

  public CreatingInstanceFailedException(Class<? extends FunctionDefinition> klass, Throwable e) {
    super("Plugin error: Creating instance of " + klass.getCanonicalName()
        + " failed with message:\n" + e.getMessage());
    this.klass = klass;
  }

  public Class<? extends FunctionDefinition> classThatFailed() {
    return klass;
  }
}
