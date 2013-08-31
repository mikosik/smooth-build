package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class TooManyParamsInExecuteMethodException extends FunctionImplementationException {
  public TooManyParamsInExecuteMethodException(Method method) {
    super(method, "It has more than one parameter but should have exactly one.");
  }
}
