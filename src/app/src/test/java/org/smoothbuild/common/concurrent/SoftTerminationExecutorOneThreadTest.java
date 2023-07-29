package org.smoothbuild.common.concurrent;

public class SoftTerminationExecutorOneThreadTest extends AbstractSoftTerminationExecutorTestSuite {
  @Override
  protected int threadCount() {
    return 1;
  }
}
