package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.Function;

@SuppressWarnings("serial")
public class TooManyConstructorParamsException extends FunctionImplementationException {
  public TooManyConstructorParamsException(Class<? extends Function> klass) {
    super(klass, "The only public constructor should have zero or one parameter.");
  }
}
