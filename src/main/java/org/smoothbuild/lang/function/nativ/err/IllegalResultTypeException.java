package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

import com.google.inject.TypeLiteral;

public class IllegalResultTypeException extends NativeFunctionImplementationException {
  public IllegalResultTypeException(Method method, TypeLiteral<?> resultType) {
    super(method, "It has is illegal result type '" + resultType + ".");
  }
}
