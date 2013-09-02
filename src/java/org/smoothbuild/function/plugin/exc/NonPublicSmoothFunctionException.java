package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class NonPublicSmoothFunctionException extends FunctionImplementationException {
  public NonPublicSmoothFunctionException(Method method) {
    super(method, "It should be public");
  }
}
