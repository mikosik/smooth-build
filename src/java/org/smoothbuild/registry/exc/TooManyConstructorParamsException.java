package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.FunctionDefinition;

@SuppressWarnings("serial")
public class TooManyConstructorParamsException extends FunctionImplementationException {
  public TooManyConstructorParamsException(Class<? extends FunctionDefinition> klass) {
    super(klass, "The only public constructor should have zero or one parameter.");
  }
}
