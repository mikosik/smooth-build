package org.smoothbuild.function.plugin.exc;

import java.lang.reflect.Method;

import org.smoothbuild.plugin.SmoothFunction;

@SuppressWarnings("serial")
public class ParamsIsNotInterfaceException extends ParamsImplementationException {

  public ParamsIsNotInterfaceException(Class<?> klass, Method method) {
    super(klass, "The only parameter of " + method.getName() + " annotated with @"
        + SmoothFunction.class.getName() + " must be interface.");
  }
}
