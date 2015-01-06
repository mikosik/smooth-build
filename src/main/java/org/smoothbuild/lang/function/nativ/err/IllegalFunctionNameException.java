package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class IllegalFunctionNameException extends NativeFunctionImplementationException {
  public IllegalFunctionNameException(Method method, String name) {
    super(method, "Its name " + name + " is illegal.");
  }
}
