package org.smoothbuild.function.nativ.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class NonStaticSmoothFunctionException extends FunctionImplementationException {
  public NonStaticSmoothFunctionException(Method method) {
    super(method, "Method should be static.");
  }
}
