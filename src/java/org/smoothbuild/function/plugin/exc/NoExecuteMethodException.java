package org.smoothbuild.function.plugin.exc;

import org.smoothbuild.plugin.ExecuteMethod;

@SuppressWarnings("serial")
public class NoExecuteMethodException extends FunctionClassImplementationException {

  public NoExecuteMethodException(Class<?> klass) {
    super(klass, "No Method annotated with @" + ExecuteMethod.class.getName() + " is found");
  }
}
