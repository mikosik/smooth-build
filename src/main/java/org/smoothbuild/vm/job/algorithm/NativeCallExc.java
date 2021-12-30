package org.smoothbuild.vm.job.algorithm;

public class NativeCallExc extends Exception {
  public NativeCallExc(String message, Throwable cause) {
    super(message, cause);
  }
}
