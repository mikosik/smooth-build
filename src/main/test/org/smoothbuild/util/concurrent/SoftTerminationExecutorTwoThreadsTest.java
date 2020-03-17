package org.smoothbuild.util.concurrent;

public class SoftTerminationExecutorTwoThreadsTest extends AbstractSoftTerminationExecutorTestCase {
  @Override
  protected SoftTerminationExecutor createJobExecutor() {
    return new SoftTerminationExecutor(2);
  }
}
