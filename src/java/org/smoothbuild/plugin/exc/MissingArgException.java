package org.smoothbuild.plugin.exc;

@SuppressWarnings("serial")
public class MissingArgException extends ParamException {
  public MissingArgException(String paramName) {
    super(paramName, "Requrired parameter '" + paramName + "' is not specified.");
  }
}
