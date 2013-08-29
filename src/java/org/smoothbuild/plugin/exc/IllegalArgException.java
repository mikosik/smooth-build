package org.smoothbuild.plugin.exc;

@SuppressWarnings("serial")
public class IllegalArgException extends ParamException {
  public IllegalArgException(String paramName, String message) {
    super(paramName, "Parameter '" + paramName + "' has been set to illegal value. " + message);
  }
}
