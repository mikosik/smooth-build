package org.smoothbuild.util.concurrent;

public class SoftTerminationExecutorOneThreadTest extends AbstractSoftTerminationExecutorTestCase {
  @Override
  protected int threadCount() {
    return 1;
  }
}
