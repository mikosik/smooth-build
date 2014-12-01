package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class IllegalFunctionNameException extends FunctionImplementationException {
  public IllegalFunctionNameException(Method method, String message) {
    super(method, message);
  }
}
