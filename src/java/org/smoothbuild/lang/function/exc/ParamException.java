package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.function.Param;

@SuppressWarnings("serial")
public class ParamException extends FunctionException {
  private final Param<?> param;

  public ParamException(Param<?> param, String message) {
    super(message);
    this.param = param;
  }

  public Param<?> param() {
    return param;
  }
}
