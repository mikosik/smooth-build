package org.smoothbuild.function.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class ParamMethodHasArgumentsException extends ParamsImplementationException {

  public ParamMethodHasArgumentsException(Class<?> klass, Method method) {
    super(klass, "Method " + method.getName() + " has parameters but should be parameterless");
  }
}
