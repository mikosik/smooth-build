package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class NonStaticSmoothFunctionException extends NativeFunctionImplementationException {
  public NonStaticSmoothFunctionException(Method method) {
    super(method, "Method should be static.");
  }
}
