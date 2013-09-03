package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

import org.smoothbuild.function.base.Type;

import com.google.inject.TypeLiteral;

@SuppressWarnings("serial")
public class IllegalReturnTypeException extends FunctionImplementationException {
  public IllegalReturnTypeException(Method method, TypeLiteral<?> returnType) {
    super(method, "It has is illegal return type '" + returnType
        + "'.\n Only following types are allowed: " + Type.javaTypesAllowedForResult() + ".");
  }
}
