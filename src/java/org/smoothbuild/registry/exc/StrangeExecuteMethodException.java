package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.FunctionDefinition;

@SuppressWarnings("serial")
public class StrangeExecuteMethodException extends FunctionImplementationException {

  public StrangeExecuteMethodException(Class<? extends FunctionDefinition> klass, Throwable e) {
    super(klass, "Exception thrown while reflexively checking execute() method.", e);
  }
}
