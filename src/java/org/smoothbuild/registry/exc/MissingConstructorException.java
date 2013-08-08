package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.FunctionDefinition;

@SuppressWarnings("serial")
public class MissingConstructorException extends FunctionImplementationException {

  public MissingConstructorException(Class<? extends FunctionDefinition> klass) {
    super(klass,
        "Exactly one public constructor should be present but class has zero public constructors.");
  }
}
