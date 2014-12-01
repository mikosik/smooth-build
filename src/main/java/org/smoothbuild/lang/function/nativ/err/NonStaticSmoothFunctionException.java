package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class NonStaticSmoothFunctionException extends FunctionImplementationException {
  public NonStaticSmoothFunctionException(Method method) {
    super(method, "Method should be static.");
  }
}
