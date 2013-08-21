package org.smoothbuild.function.exc;


@SuppressWarnings("serial")
public class CreatingInstanceFailedException extends FunctionReflectionException {
  private final Class<?> klass;

  public CreatingInstanceFailedException(Class<?> klass, Throwable e) {
    super("Plugin error: Creating instance of " + klass.getCanonicalName()
        + " failed with message:\n" + e.getMessage());
    this.klass = klass;
  }

  public Class<?> classThatFailed() {
    return klass;
  }
}
