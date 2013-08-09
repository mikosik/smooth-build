package org.smoothbuild.registry;

@SuppressWarnings("serial")
public class SmoothFatalException extends RuntimeException {
  public SmoothFatalException(Throwable e) {
    super(e);
  }
}
