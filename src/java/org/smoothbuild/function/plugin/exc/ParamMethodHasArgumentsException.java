package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class ParamMethodHasArgumentsException extends ParamsImplementationException {
  public ParamMethodHasArgumentsException(Method method, Method paramMethod) {
    super(method, "with all method parameterless, but method " + paramMethod.getName()
        + " has parameters.");
  }
}
