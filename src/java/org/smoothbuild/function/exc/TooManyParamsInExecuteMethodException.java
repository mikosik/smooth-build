package org.smoothbuild.function.exc;

import org.smoothbuild.lang.type.ExecuteMethod;

@SuppressWarnings("serial")
public class TooManyParamsInExecuteMethodException extends FunctionImplementationException {

  public TooManyParamsInExecuteMethodException(Class<?> klass) {
    super(klass, "Method annotated with @" + ExecuteMethod.class.getName()
        + " has more than one parameter but should have exactly one");
  }
}
