package org.smoothbuild.util.concurrent;

public class SoftTerminationExecutorTwoThreadsTest extends AbstractSoftTerminationExecutorTestCase {
  @Override
  protected int threadCount() {
    return 2;
  }
}
