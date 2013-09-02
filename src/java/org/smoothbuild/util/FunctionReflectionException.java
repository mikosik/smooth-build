package org.smoothbuild.util;

import org.smoothbuild.plugin.exc.FunctionException;

@SuppressWarnings("serial")
public class FunctionReflectionException extends FunctionException {
  public FunctionReflectionException(String message) {
    super(message);
  }
}
