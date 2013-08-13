package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.FunctionDefinition;

@SuppressWarnings("serial")
public class IllegalFunctionNameException extends FunctionImplementationException {
  public IllegalFunctionNameException(Class<? extends FunctionDefinition> klass, String message) {
    super(klass, message);
  }
}
