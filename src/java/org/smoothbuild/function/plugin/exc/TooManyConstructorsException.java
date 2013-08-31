package org.smoothbuild.function.plugin.exc;

@SuppressWarnings("serial")
public class TooManyConstructorsException extends FunctionClassImplementationException {

  public TooManyConstructorsException(Class<?> klass) {
    super(klass,
        "Exactly one public constructor should be present but class has more than one public constructor.");
  }
}
