package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

import org.smoothbuild.plugin.SmoothMethod;

@SuppressWarnings("serial")
public class ParamsIsNotInterfaceException extends ParamsImplementationException {

  public ParamsIsNotInterfaceException(Class<?> klass, Method method) {
    super(klass, "The only parameter of " + method.getName() + " annotated with @"
        + SmoothMethod.class.getName() + " must be interface.");
  }
}
