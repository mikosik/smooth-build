package org.smoothbuild.registry.exc;


@SuppressWarnings("serial")
public class TooManyConstructorParamsException extends FunctionImplementationException {
  public TooManyConstructorParamsException(Class<?> klass) {
    super(klass, "The only public constructor should have zero or one parameter.");
  }
}
