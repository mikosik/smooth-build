package org.smoothbuild.lang.function.exc;


@SuppressWarnings("serial")
public class IllegalArgException extends ParamException {
  public IllegalArgException(String paramName, String message) {
    super(paramName, "Parameter '' has been set to illegal value. " + message);
  }
}
