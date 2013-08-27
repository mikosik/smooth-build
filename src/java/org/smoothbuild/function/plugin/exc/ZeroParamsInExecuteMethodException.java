package org.smoothbuild.function.plugin.exc;

import org.smoothbuild.plugin.ExecuteMethod;

@SuppressWarnings("serial")
public class ZeroParamsInExecuteMethodException extends FunctionImplementationException {

  public ZeroParamsInExecuteMethodException(Class<?> klass) {
    super(klass, "Method annotated with @" + ExecuteMethod.class.getName()
        + " has zero parameters but should have exactly one");
  }
}
