package org.smoothbuild.function.plugin.exc;


@SuppressWarnings("serial")
public class MissingConstructorException extends FunctionImplementationException {

  public MissingConstructorException(Class<?> klass) {
    super(klass,
        "Exactly one public constructor should be present but class has zero public constructors.");
  }
}
