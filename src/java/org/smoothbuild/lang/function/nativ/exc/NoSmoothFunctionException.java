package org.smoothbuild.lang.function.nativ.exc;

import org.smoothbuild.lang.plugin.SmoothFunction;

@SuppressWarnings("serial")
public class NoSmoothFunctionException extends FunctionClassImplementationException {

  public NoSmoothFunctionException(Class<?> klass) {
    super(klass, "No method annotated with @" + SmoothFunction.class.getName() + " is found");
  }
}
