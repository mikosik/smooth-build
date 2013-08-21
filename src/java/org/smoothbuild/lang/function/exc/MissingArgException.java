package org.smoothbuild.lang.function.exc;

@SuppressWarnings("serial")
public class MissingArgException extends ParamException {
  public MissingArgException(String paramName) {
    super(paramName, "Requrired parameter '" + paramName + "' is not specified.");
  }
}
