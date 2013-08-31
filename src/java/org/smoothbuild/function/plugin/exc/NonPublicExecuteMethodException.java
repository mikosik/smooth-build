package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class NonPublicExecuteMethodException extends FunctionImplementationException {
  public NonPublicExecuteMethodException(Method method) {
    super(method, "It should be public");
  }
}
