package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class IllegalParameterNameException extends FunctionImplementationException {
  public IllegalParameterNameException(Method method, String name) {
    super(method, "One of its parameter has illegal name '" + name + "'.");
  }
}
