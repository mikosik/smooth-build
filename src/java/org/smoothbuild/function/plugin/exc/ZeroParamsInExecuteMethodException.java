package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class ZeroParamsInExecuteMethodException extends FunctionImplementationException {
  public ZeroParamsInExecuteMethodException(Method method) {
    super(method, "It has zero parameters but should have exactly one.");
  }
}
