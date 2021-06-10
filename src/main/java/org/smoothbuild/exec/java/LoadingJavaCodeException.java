package org.smoothbuild.exec.java;

public class LoadingJavaCodeException extends Exception {
  public LoadingJavaCodeException(String message, Throwable e) {
    super(message, e);
  }

  public LoadingJavaCodeException(String message) {
    super(message);
  }
}
