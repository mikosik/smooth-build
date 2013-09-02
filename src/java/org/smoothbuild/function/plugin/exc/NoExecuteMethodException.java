package org.smoothbuild.function.plugin.exc;

import org.smoothbuild.plugin.SmoothFunction;

@SuppressWarnings("serial")
public class NoExecuteMethodException extends FunctionClassImplementationException {

  public NoExecuteMethodException(Class<?> klass) {
    super(klass, "No Method annotated with @" + SmoothFunction.class.getName() + " is found");
  }
}
