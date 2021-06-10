package org.smoothbuild.exec.nativ;

public class LoadingNativeException extends Exception {
  public LoadingNativeException(String message, Throwable e) {
    super(message, e);
  }

  public LoadingNativeException(String message) {
    super(message);
  }
}
