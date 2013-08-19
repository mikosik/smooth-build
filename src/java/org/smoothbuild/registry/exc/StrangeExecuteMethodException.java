package org.smoothbuild.registry.exc;


@SuppressWarnings("serial")
public class StrangeExecuteMethodException extends FunctionImplementationException {

  public StrangeExecuteMethodException(Class<?> klass, Throwable e) {
    super(klass, "Exception thrown while reflexively checking execute() method.", e);
  }
}
