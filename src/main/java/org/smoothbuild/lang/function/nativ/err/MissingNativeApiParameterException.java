package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

import org.smoothbuild.lang.plugin.NativeApi;

public class MissingNativeApiParameterException extends FunctionImplementationException {
  public MissingNativeApiParameterException(Method method) {
    super(method, "Its first parameter should have '" + NativeApi.class.getCanonicalName()
        + "' type.");
  }
}
