package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class NativeFunctionImplementationException extends RuntimeException {

  public NativeFunctionImplementationException(Method method, String message) {
    super(buildMessage(method, message));
  }

  public NativeFunctionImplementationException(Method method, String message, Throwable e) {
    super(buildMessage(method, message), e);
  }

  private static String buildMessage(Method method, String message) {
    return "Java method '" + method.getDeclaringClass().getCanonicalName() + "." + method.getName()
        + "' in not a correct Smooth function implementation:\n" + message;
  }
}
