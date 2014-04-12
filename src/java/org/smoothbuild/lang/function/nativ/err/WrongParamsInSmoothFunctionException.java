package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

import org.smoothbuild.lang.plugin.NativeApi;

@SuppressWarnings("serial")
public class WrongParamsInSmoothFunctionException extends FunctionImplementationException {
  public WrongParamsInSmoothFunctionException(Method method) {
    super(method, "It should contains exactly two parameters. First of type '"
        + NativeApi.class.getCanonicalName()
        + "' second should be an interface specyfing smooth function parameters");
  }
}
