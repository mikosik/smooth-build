package org.smoothbuild.util.concurrent;

import static com.google.common.truth.Truth.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public abstract class AbstractSoftTerminationExecutorTestSuite {
  private SoftTerminationExecutor executor;

  @BeforeEach
  public void before() {
    executor = new SoftTerminationExecutor(threadCount());
  }

  protected abstract int threadCount();

  @Test
  public void submitted_runnable_gets_executed() throws Exception {
    Completable completable = new Completable() ;
    executor.enqueue(completable);
    executor.terminate();
    executor.awaitTermination();

    assertThat(completable.isCompleted())
        .isTrue();
  }

  @Test
  public void empty_executor_can_be_terminated() throws Exception {
    executor.terminate();
    executor.awaitTermination();
  }

  @Test
  public void executor_can_be_terminated_twice() throws Exception {
    executor.terminate();
    executor.terminate();
    executor.awaitTermination();
  }

  @Test
  public void runnable_can_terminate_executor() throws Exception {
    executor.enqueue(executor::terminate);
    executor.awaitTermination();
  }

  @Test
  public void runnable_can_enqueue_another_runnable() throws Exception {
    executor.enqueue(() -> executor.enqueue(executor::terminate));
    executor.awaitTermination();
  }

  @Test
  public void client_can_terminate_executor() throws Exception {
    executor.terminate();
    executor.awaitTermination();
  }

  @Test
  public void runnable_submitted_after_termination_is_ignored() throws InterruptedException {
    Completable completable = new Completable();
    executor.terminate();
    executor.enqueue(completable);
    executor.awaitTermination();
    assertThat(completable.isCompleted())
        .isFalse();
  }

  private static class Completable implements Runnable {
    AtomicBoolean completed = new AtomicBoolean(false);

    @Override
    public void run() {
      completed.set(true);
    }

    public boolean isCompleted() {
      return completed.get();
    }
  }
}
