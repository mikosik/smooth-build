package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class IllegalParameterTypeException extends NativeFunctionImplementationException {
  public IllegalParameterTypeException(Method method, java.lang.reflect.Type type) {
    super(method, "It has parameter with illegal type '" + type + "'.");
  }
}
