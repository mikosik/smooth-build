package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

public class DuplicatedParameterException extends FunctionImplementationException {
  public DuplicatedParameterException(Method method, String name) {
    super(method, "It has two parameters with name '" + name + "'.");
  }
}
