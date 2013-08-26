package org.smoothbuild.function.exc;

import org.smoothbuild.function.Type;

@SuppressWarnings("serial")
public class IllegalReturnTypeException extends FunctionImplementationException {

  public IllegalReturnTypeException(Class<?> klass, Class<?> returnType) {
    super(klass, "Return type of execute() method is illegal '" + returnType.getCanonicalName()
        + "'.\n Only following types are allowed: " + Type.javaTypesAllowedForResult() + ".");
  }
}
