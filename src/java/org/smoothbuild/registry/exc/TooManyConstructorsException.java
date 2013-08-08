package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.FunctionDefinition;

@SuppressWarnings("serial")
public class TooManyConstructorsException extends FunctionImplementationException {

  public TooManyConstructorsException(Class<? extends FunctionDefinition> klass) {
    super(klass,
        "Exactly one public constructor should be present but class has more than one public constructor.");
  }
}
