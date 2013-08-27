package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

import org.smoothbuild.function.base.Type;

@SuppressWarnings("serial")
public class ForbiddenParamTypeException extends ParamsImplementationException {

  public ForbiddenParamTypeException(Class<?> klass, Method method, Class<?> paramType) {
    super(klass, "Method " + method.getName() + " has forbidden return type = "
        + paramType.getCanonicalName() + "\n Allowed types = " + Type.javaTypesAllowedForParam());
  }
}
