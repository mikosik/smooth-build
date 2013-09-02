package org.smoothbuild.function.plugin.exc;

import org.smoothbuild.plugin.SmoothFunction;

@SuppressWarnings("serial")
public class MoreThanOneExecuteMethodException extends FunctionClassImplementationException {

  public MoreThanOneExecuteMethodException(Class<?> klass) {
    super(klass, "More than one method annotated with @" + SmoothFunction.class.getName()
        + "\n found");
  }
}
