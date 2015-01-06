package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class NonPublicSmoothFunctionException extends NativeFunctionImplementationException {
  public NonPublicSmoothFunctionException(Method method) {
    super(method, "It should be public");
  }
}
