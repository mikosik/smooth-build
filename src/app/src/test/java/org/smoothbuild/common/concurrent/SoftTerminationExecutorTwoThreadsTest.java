package org.smoothbuild.common.concurrent;

public class SoftTerminationExecutorTwoThreadsTest extends
    AbstractSoftTerminationExecutorTestSuite {
  @Override
  protected int threadCount() {
    return 2;
  }
}
