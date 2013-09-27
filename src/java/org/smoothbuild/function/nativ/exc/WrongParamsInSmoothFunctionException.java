package org.smoothbuild.function.nativ.exc;

import java.lang.reflect.Method;

import org.smoothbuild.plugin.api.Sandbox;

@SuppressWarnings("serial")
public class WrongParamsInSmoothFunctionException extends FunctionImplementationException {
  public WrongParamsInSmoothFunctionException(Method method) {
    super(method, "It should contains exactly two parameters. First of type '"
        + Sandbox.class.getCanonicalName()
        + "' second should be an interface specyfing smooth function parameters");
  }
}
