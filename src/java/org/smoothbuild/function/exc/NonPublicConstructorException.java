package org.smoothbuild.function.exc;

@SuppressWarnings("serial")
public class NonPublicConstructorException extends FunctionImplementationException {

  public NonPublicConstructorException(Class<?> klass) {
    super(klass, "Constructor should be public.");
  }
}
