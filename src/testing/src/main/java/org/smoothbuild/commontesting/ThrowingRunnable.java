package org.smoothbuild.commontesting;

@FunctionalInterface
public interface ThrowingRunnable {
  public void run() throws Exception;
}
