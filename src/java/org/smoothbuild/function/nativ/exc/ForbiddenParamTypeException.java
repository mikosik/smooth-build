package org.smoothbuild.function.nativ.exc;

import java.lang.reflect.Method;

import org.smoothbuild.function.base.Type;

import com.google.inject.TypeLiteral;

@SuppressWarnings("serial")
public class ForbiddenParamTypeException extends ParamsImplementationException {

  public ForbiddenParamTypeException(Method method, Method paramMethod, TypeLiteral<?> javaType) {
    super(method, "with all methods returning proper types, but method '" + method.getName()
        + "' has forbidden return type = " + javaType + "\n Allowed types = "
        + Type.javaTypesAllowedForParam());
  }
}
