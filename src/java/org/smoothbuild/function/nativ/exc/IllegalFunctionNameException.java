package org.smoothbuild.function.nativ.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class IllegalFunctionNameException extends FunctionImplementationException {
  public IllegalFunctionNameException(Method method, String message) {
    super(method, message);
  }
}
