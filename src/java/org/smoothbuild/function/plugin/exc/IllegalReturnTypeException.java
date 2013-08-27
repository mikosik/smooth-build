package org.smoothbuild.function.plugin.exc;

import org.smoothbuild.function.base.Type;

@SuppressWarnings("serial")
public class IllegalReturnTypeException extends FunctionImplementationException {

  public IllegalReturnTypeException(Class<?> klass, Class<?> returnType) {
    super(klass, "Return type of execute() method is illegal '" + returnType.getCanonicalName()
        + "'.\n Only following types are allowed: " + Type.javaTypesAllowedForResult() + ".");
  }
}
