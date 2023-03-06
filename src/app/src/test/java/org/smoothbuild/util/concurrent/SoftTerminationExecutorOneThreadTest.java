package org.smoothbuild.util.concurrent;

public class SoftTerminationExecutorOneThreadTest extends AbstractSoftTerminationExecutorTestSuite {
  @Override
  protected int threadCount() {
    return 1;
  }
}
