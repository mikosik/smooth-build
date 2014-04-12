package org.smoothbuild.lang.function.nativ.err;

import static org.smoothbuild.lang.type.STypes.javaTypesAllowedForResult;

import java.lang.reflect.Method;

import com.google.inject.TypeLiteral;

@SuppressWarnings("serial")
public class IllegalReturnTypeException extends FunctionImplementationException {
  public IllegalReturnTypeException(Method method, TypeLiteral<?> returnType) {
    super(method, "It has is illegal return type '" + returnType
        + "'.\n Only following types are allowed: " + javaTypesAllowedForResult() + ".");
  }
}
