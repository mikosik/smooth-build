package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class ParamsIsNotInterfaceException extends FunctionImplementationException {
  public ParamsIsNotInterfaceException(Method method) {
    super(method, "Its second parameter must be interface.");
  }
}
