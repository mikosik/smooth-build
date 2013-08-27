package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class InvokingMethodFailedException extends FunctionReflectionException {
  private final Method method;

  public InvokingMethodFailedException(Method method, Throwable e) {
    super("Plugin error: Invoking method " + method.getName() + " on class "
        + method.getDeclaringClass().getCanonicalName() + " failed." + e.getMessage());
    this.method = method;
  }

  public Method methodThatFailed() {
    return method;
  }
}
