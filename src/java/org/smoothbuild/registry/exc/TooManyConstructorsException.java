package org.smoothbuild.registry.exc;


@SuppressWarnings("serial")
public class TooManyConstructorsException extends FunctionImplementationException {

  public TooManyConstructorsException(Class<?> klass) {
    super(klass,
        "Exactly one public constructor should be present but class has more than one public constructor.");
  }
}
