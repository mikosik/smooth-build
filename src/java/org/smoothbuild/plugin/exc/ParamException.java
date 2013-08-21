package org.smoothbuild.plugin.exc;

@SuppressWarnings("serial")
public class ParamException extends FunctionException {
  private final String paramName;

  public ParamException(String paramName, String message) {
    super(message);
    this.paramName = paramName;
  }

  public String paramName() {
    return paramName;
  }
}
