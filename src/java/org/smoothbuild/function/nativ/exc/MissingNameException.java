package org.smoothbuild.function.nativ.exc;

import java.lang.reflect.Method;

import org.smoothbuild.plugin.SmoothFunction;

@SuppressWarnings("serial")
public class MissingNameException extends FunctionImplementationException {
  public MissingNameException(Method method) {
    super(method, "It should be annotated with @" + SmoothFunction.class.getCanonicalName() + ".");
  }
}
