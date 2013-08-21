package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.ExecuteMethod;

@SuppressWarnings("serial")
public class NoExecuteMethodException extends FunctionImplementationException {

  public NoExecuteMethodException(Class<?> klass) {
    super(klass, "No Method annotated with @" + ExecuteMethod.class.getName() + " is found");
  }
}
