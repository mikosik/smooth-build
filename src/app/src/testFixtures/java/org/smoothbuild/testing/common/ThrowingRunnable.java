package org.smoothbuild.testing.common;

@FunctionalInterface
public interface ThrowingRunnable {
  public void run() throws Exception;
}
