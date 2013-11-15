package org.smoothbuild.lang.function.nativ.exc;

import org.smoothbuild.lang.plugin.SmoothFunction;

@SuppressWarnings("serial")
public class MoreThanOneSmoothFunctionException extends FunctionClassImplementationException {

  public MoreThanOneSmoothFunctionException(Class<?> klass) {
    super(klass, "More than one method annotated with @" + SmoothFunction.class.getName()
        + "\n found");
  }
}
