package org.smoothbuild.exec.nativ;

public class LoadingNativeJarException extends Exception {
  public LoadingNativeJarException(String message, Throwable e) {
    super(message, e);
  }

  public LoadingNativeJarException(String message) {
    super(message);
  }
}
