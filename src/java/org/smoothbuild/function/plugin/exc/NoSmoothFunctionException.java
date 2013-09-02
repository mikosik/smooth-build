package org.smoothbuild.function.plugin.exc;

import org.smoothbuild.plugin.SmoothFunction;

@SuppressWarnings("serial")
public class NoSmoothFunctionException extends FunctionClassImplementationException {

  public NoSmoothFunctionException(Class<?> klass) {
    super(klass, "No method annotated with @" + SmoothFunction.class.getName() + " is found");
  }
}
