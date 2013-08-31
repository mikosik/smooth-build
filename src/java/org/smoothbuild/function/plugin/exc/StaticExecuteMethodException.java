package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class StaticExecuteMethodException extends FunctionImplementationException {
  public StaticExecuteMethodException(Method method) {
    super(method, "Method should not be static.");
  }
}
