package org.smoothbuild.registry.exc;

import org.smoothbuild.lang.function.exc.FunctionException;

@SuppressWarnings("serial")
public class FunctionReflectionException extends FunctionException {
  public FunctionReflectionException(String message) {
    super(message);
  }
}
