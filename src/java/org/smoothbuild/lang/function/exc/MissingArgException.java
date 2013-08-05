package org.smoothbuild.lang.function.exc;

import org.smoothbuild.lang.function.Param;

@SuppressWarnings("serial")
public class MissingArgException extends ParamException {
  public MissingArgException(Param<?> param) {
    super(param, "Requrired parameter '" + param.name() + "' is not specified.");
  }
}
