package org.smoothbuild.lang.function.nativ.err;

@SuppressWarnings("serial")
public class NativeImplementationException extends Exception {
  public NativeImplementationException(String message) {
    super(message);
  }

  public NativeImplementationException(String message, Throwable e) {
    super(message, e);
  }
}
