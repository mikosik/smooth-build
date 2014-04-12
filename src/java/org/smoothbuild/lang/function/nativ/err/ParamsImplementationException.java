package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

@SuppressWarnings("serial")
public class ParamsImplementationException extends FunctionImplementationException {
  public ParamsImplementationException(Method method, String message) {
    super(method, "Its second argument should be an interface,\n" + message);
  }
}
