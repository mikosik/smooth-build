package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.ExecuteMethod;

@SuppressWarnings("serial")
public class ZeroParamsInExecuteMethodException extends FunctionImplementationException {

  public ZeroParamsInExecuteMethodException(Class<?> klass) {
    super(klass, "Method annotated with @" + ExecuteMethod.class.getName()
        + " has zero parameters but should have exactly one");
  }
}
