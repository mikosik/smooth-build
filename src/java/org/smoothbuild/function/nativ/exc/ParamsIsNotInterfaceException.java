package org.smoothbuild.function.nativ.exc;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class ParamsIsNotInterfaceException extends FunctionImplementationException {
  public ParamsIsNotInterfaceException(Method method) {
    super(method, "Its second parameter must be interface.");
  }
}
