package org.smoothbuild.exec.nativ;

public class LoadingNativeImplException extends Exception {
  public LoadingNativeImplException(String message, Throwable e) {
    super(message, e);
  }

  public LoadingNativeImplException(String message) {
    super(message);
  }
}
