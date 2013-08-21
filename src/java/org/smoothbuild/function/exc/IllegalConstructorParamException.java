package org.smoothbuild.function.exc;

import org.smoothbuild.lang.type.Files;

@SuppressWarnings("serial")
public class IllegalConstructorParamException extends FunctionImplementationException {

  public IllegalConstructorParamException(Class<?> klass, Class<?> paramType) {
    super(klass, "The only public constructor has parameter with illegal type '"
        + paramType.getCanonicalName() + "'. Only " + Files.class.getCanonicalName()
        + " is allowed.");
  }
}
