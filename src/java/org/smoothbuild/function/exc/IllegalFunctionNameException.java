package org.smoothbuild.function.exc;


@SuppressWarnings("serial")
public class IllegalFunctionNameException extends FunctionImplementationException {
  public IllegalFunctionNameException(Class<?> klass, String message) {
    super(klass, message);
  }
}
