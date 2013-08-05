package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.function.Param;

@SuppressWarnings("serial")
public class IllegalArgException extends ParamException {
  public IllegalArgException(Param<?> param, String message) {
    super(param, "Parameter '' has been set to illegal value. " + message);
  }
}
