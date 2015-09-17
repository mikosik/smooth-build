package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

import org.smoothbuild.lang.plugin.Container;

public class MissingContainerParameterException extends NativeFunctionImplementationException {
  public MissingContainerParameterException(Method method) {
    super(method, "Its first parameter should have '" + Container.class.getCanonicalName()
        + "' type.");
  }
}
