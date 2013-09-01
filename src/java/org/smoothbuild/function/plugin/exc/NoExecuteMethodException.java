package org.smoothbuild.function.plugin.exc;

import org.smoothbuild.plugin.SmoothMethod;

@SuppressWarnings("serial")
public class NoExecuteMethodException extends FunctionClassImplementationException {

  public NoExecuteMethodException(Class<?> klass) {
    super(klass, "No Method annotated with @" + SmoothMethod.class.getName() + " is found");
  }
}
