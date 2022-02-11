package org.smoothbuild.vm.java;

public class MethodProvExc extends Exception {
  public MethodProvExc(String message, Throwable cause) {
    super(message, cause);
  }

  public MethodProvExc(String message) {
    super(message);
  }
}
