package org.smoothbuild.lang.function.nativ.err;

import java.lang.reflect.Method;

import org.smoothbuild.lang.plugin.SmoothFunction;

@SuppressWarnings("serial")
public class MissingNameException extends FunctionImplementationException {
  public MissingNameException(Method method) {
    super(method, "It should be annotated with @" + SmoothFunction.class.getCanonicalName() + ".");
  }
}
