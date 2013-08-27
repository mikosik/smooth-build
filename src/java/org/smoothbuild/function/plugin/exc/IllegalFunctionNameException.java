package org.smoothbuild.function.plugin.exc;


@SuppressWarnings("serial")
public class IllegalFunctionNameException extends FunctionImplementationException {
  public IllegalFunctionNameException(Class<?> klass, String message) {
    super(klass, message);
  }
}
