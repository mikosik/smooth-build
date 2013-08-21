package org.smoothbuild.function.exc;

import java.lang.reflect.Method;

import org.smoothbuild.lang.type.ExecuteMethod;

@SuppressWarnings("serial")
public class ParamsIsNotInterfaceException extends ParamsImplementationException {

  public ParamsIsNotInterfaceException(Class<?> klass, Method method) {
    super(klass, "The only parameter of " + method.getName() + " annotated with @"
        + ExecuteMethod.class.getName() + " must be interface.");
  }
}
