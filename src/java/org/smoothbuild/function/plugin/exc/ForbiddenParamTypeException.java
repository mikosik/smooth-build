package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

import org.smoothbuild.function.base.Type;

@SuppressWarnings("serial")
public class ForbiddenParamTypeException extends ParamsImplementationException {

  public ForbiddenParamTypeException(Method method, Method paramMethod, Class<?> paramType) {
    super(method, "with all methods returning proper types, but method '" + method.getName()
        + "' has forbidden return type = " + paramType.getCanonicalName() + "\n Allowed types = "
        + Type.javaTypesAllowedForParam());
  }
}
