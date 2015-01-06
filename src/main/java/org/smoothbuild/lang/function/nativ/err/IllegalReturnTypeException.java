package org.smoothbuild.lang.function.nativ.err;

import static org.smoothbuild.lang.type.Types.resultJTypes;

import java.lang.reflect.Method;

import com.google.inject.TypeLiteral;

public class IllegalReturnTypeException extends NativeFunctionImplementationException {
  public IllegalReturnTypeException(Method method, TypeLiteral<?> returnType) {
    super(method, "It has is illegal return type '" + returnType
        + "'.\n Only following types are allowed: " + resultJTypes() + ".");
  }
}
