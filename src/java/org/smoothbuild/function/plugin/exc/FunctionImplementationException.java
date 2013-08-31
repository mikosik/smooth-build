package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class FunctionImplementationException extends PluginImplementationException {

  public FunctionImplementationException(Method method, String message) {
    super(buildMessage(method, message));
  }

  public FunctionImplementationException(Method method, String message, Throwable e) {
    super(buildMessage(method, message), e);
  }

  private static String buildMessage(Method method, String message) {
    return "Java method '" + method.getDeclaringClass().getCanonicalName() + "." + method.getName()
        + "' in not a correct Smooth function implementation:\n" + message;
  }
}
