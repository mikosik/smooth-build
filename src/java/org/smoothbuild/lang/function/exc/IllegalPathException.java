package org.smoothbuild.lang.function.exc;


@SuppressWarnings("serial")
public class IllegalPathException extends IllegalArgException {
  public IllegalPathException(String paramName, String message) {
    super(paramName, message);
  }
}
