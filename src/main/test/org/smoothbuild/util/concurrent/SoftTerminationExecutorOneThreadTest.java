package org.smoothbuild.util.concurrent;

public class SoftTerminationExecutorOneThreadTest extends AbstractSoftTerminationExecutorTestCase {
  @Override
  protected SoftTerminationExecutor createJobExecutor() {
    return new SoftTerminationExecutor(1);
  }
}
