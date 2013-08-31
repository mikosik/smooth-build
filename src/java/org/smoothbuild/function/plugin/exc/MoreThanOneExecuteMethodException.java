package org.smoothbuild.function.plugin.exc;

import org.smoothbuild.plugin.ExecuteMethod;

@SuppressWarnings("serial")
public class MoreThanOneExecuteMethodException extends FunctionClassImplementationException {

  public MoreThanOneExecuteMethodException(Class<?> klass) {
    super(klass, "More than one method annotated with @" + ExecuteMethod.class.getName()
        + "\n found");
  }
}
