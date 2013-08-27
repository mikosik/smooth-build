package org.smoothbuild.function.plugin.exc;

import org.smoothbuild.plugin.ExecuteMethod;

@SuppressWarnings("serial")
public class TooManyParamsInExecuteMethodException extends FunctionImplementationException {

  public TooManyParamsInExecuteMethodException(Class<?> klass) {
    super(klass, "Method annotated with @" + ExecuteMethod.class.getName()
        + " has more than one parameter but should have exactly one");
  }
}
