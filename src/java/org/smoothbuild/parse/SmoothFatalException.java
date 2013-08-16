package org.smoothbuild.parse;

@SuppressWarnings("serial")
public class SmoothFatalException extends RuntimeException {
  public SmoothFatalException(Throwable e) {
    super(e);
  }
}
