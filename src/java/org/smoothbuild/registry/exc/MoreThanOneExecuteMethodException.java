package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.ExecuteMethod;

@SuppressWarnings("serial")
public class MoreThanOneExecuteMethodException extends FunctionImplementationException {

  public MoreThanOneExecuteMethodException(Class<?> klass) {
    super(klass, "More than one method annotated with @" + ExecuteMethod.class.getName()
        + "\n found");
  }
}
