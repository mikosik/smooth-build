package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

import org.smoothbuild.function.base.Type;

@SuppressWarnings("serial")
public class IllegalReturnTypeException extends FunctionImplementationException {
  public IllegalReturnTypeException(Method method, Class<?> returnType) {
    super(method, "It has is illegal return type '" + returnType.getCanonicalName()
        + "'.\n Only following types are allowed: " + Type.javaTypesAllowedForResult() + ".");
  }
}
