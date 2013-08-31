package org.smoothbuild.function.plugin.exc;

@SuppressWarnings("serial")
public class TooManyConstructorParamsException extends FunctionClassImplementationException {
  public TooManyConstructorParamsException(Class<?> klass) {
    super(klass, "The only public constructor should have zero or one parameter.");
  }
}
