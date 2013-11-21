package org.smoothbuild.lang.function.nativ.exc;

import java.lang.reflect.Method;

import org.smoothbuild.lang.type.Type;

import com.google.inject.TypeLiteral;

@SuppressWarnings("serial")
public class IllegalReturnTypeException extends FunctionImplementationException {
  public IllegalReturnTypeException(Method method, TypeLiteral<?> returnType) {
    super(method, "It has is illegal return type '" + returnType
        + "'.\n Only following types are allowed: " + Type.javaTypesAllowedForResult() + ".");
  }
}
