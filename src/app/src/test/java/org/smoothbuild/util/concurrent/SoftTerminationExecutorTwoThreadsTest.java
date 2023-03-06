package org.smoothbuild.util.concurrent;

public class SoftTerminationExecutorTwoThreadsTest extends
    AbstractSoftTerminationExecutorTestSuite {
  @Override
  protected int threadCount() {
    return 2;
  }
}
