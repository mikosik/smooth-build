package org.smoothbuild.common.concurrent;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.synchronizedSet;
import static java.util.Comparator.naturalOrder;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.smoothbuild.common.testing.TestingThread.sleepMillis;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.function.Consumer0;

public class ExecutorTest {
  @Test
  void submitting_null_runnable_fails() {
    var executor = newExecutor();
    assertCall(() -> executor.submit(null)).throwsException(NullPointerException.class);
  }

  @Test
  void submitted_runnable_is_executed() throws Exception {
    var countDownLatch = new CountDownLatch(1);

    newExecutor().submit(countDownLatch::countDown);
    var countDownIsZero = countDownLatch.await(1, SECONDS);

    assertThat(countDownIsZero).isTrue();
  }

  @Test
  void submitted_runnable_is_executed_in_different_thread_than_submitting() throws Exception {
    var queue = new ArrayBlockingQueue<>(1);

    newExecutor().submit(wrapExceptionRunnable(() -> queue.put(Thread.currentThread())));

    var thread = queue.poll(1, SECONDS);
    assertThat(thread).isNotSameInstanceAs(Thread.currentThread());
  }

  @Test
  void each_submitted_runnable_is_executed_in_different_thread() throws Exception {
    var threads = synchronizedSet(new HashSet<Thread>());
    var runnablesCount = 100;
    var countDownLatch = new CountDownLatch(runnablesCount);
    Runnable runnable = () -> {
      sleepMillis(100);
      threads.add(Thread.currentThread());
      countDownLatch.countDown();
    };

    var executor = newExecutor(runnablesCount / 2);
    for (int i = 0; i < runnablesCount; i++) {
      executor.submit(runnable);
    }
    var countDownIsZero = countDownLatch.await(1, SECONDS);

    assertThat(countDownIsZero).isTrue();
    assertThat(threads.size()).isEqualTo(runnablesCount);
  }

  @Test
  void count_of_runnables_executing_at_the_same_time_is_limited_to_max_threads() throws Exception {
    var threadCount = new AtomicInteger(0);
    var threadCountHistory = synchronizedSet(new HashSet<Integer>());
    var maxThreads = 10;
    var runnablesCount = 2 * maxThreads;
    var countDownLatch = new CountDownLatch(runnablesCount);
    Runnable runnable = () -> {
      var count = threadCount.incrementAndGet();
      threadCountHistory.add(count);
      sleepMillis(100);
      threadCount.decrementAndGet();
      countDownLatch.countDown();
      // throw exception to check that throw exceptions does not affect Executor
      throw new RuntimeException();
    };

    var executor = newExecutor(maxThreads);
    for (int i = 0; i < runnablesCount; i++) {
      executor.submit(runnable);
    }
    var countDownIsZero = countDownLatch.await(1, SECONDS);

    assertThat(countDownIsZero).isTrue();
    var maxRunningThreads = threadCountHistory.stream().max(naturalOrder());
    assertThat(maxRunningThreads).isEqualTo(Optional.of(maxThreads));
  }

  @Test
  void wait_until_idle() throws Exception {
    var familyCount = 4;
    var familyCompletionCount = new AtomicInteger(0);
    var executor = newExecutor(familyCount / 2);

    for (int i = 0; i < familyCount; i++) {
      executor.submit(newCloningRunnable(executor, 100, familyCompletionCount));
    }

    executor.waitUntilIdle();
    assertThat(familyCompletionCount.get()).isEqualTo(familyCount);
  }

  private static Runnable newCloningRunnable(
      Executor executor, int count, AtomicInteger familyCompletionCount) {
    return () -> {
      sleepMillis(1);
      var nextCount = count - 1;
      if (0 < nextCount) {
        executor.submit(newCloningRunnable(executor, nextCount, familyCompletionCount));
      } else {
        familyCompletionCount.incrementAndGet();
      }
    };
  }

  private static <T extends Throwable> Runnable wrapExceptionRunnable(Consumer0<T> consumer) {
    return () -> {
      try {
        consumer.accept();
      } catch (Throwable e) {
        throw new RuntimeException(e);
      }
    };
  }

  private static Executor newExecutor() {
    return newExecutor(4);
  }

  private static Executor newExecutor(int maxThreads) {
    return new Executor(maxThreads);
  }
}
