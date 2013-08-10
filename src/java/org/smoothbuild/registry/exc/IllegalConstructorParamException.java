package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.type.Files;

@SuppressWarnings("serial")
public class IllegalConstructorParamException extends FunctionImplementationException {

  public IllegalConstructorParamException(Class<? extends FunctionDefinition> klass,
      Class<?> paramType) {
    super(klass, "The only public constructor has parameter with illegal type '"
        + paramType.getCanonicalName() + "'. Only " + Files.class.getCanonicalName()
        + " is allowed.");
  }
}
