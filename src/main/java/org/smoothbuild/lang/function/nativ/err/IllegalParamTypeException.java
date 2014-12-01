package org.smoothbuild.lang.function.nativ.err;

import static org.smoothbuild.lang.base.Types.parameterJTypes;

import java.lang.reflect.Method;

import com.google.inject.TypeLiteral;

@SuppressWarnings("serial")
public class IllegalParamTypeException extends ParamsImplementationException {

  public IllegalParamTypeException(Method method, Method paramMethod, TypeLiteral<?> jType) {
    super(method, "with all methods returning proper types, but method '" + paramMethod.getName()
        + "' has forbidden return type = " + jType + "\n Allowed types = " + parameterJTypes());
  }
}
