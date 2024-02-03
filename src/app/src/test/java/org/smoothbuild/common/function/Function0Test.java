package org.smoothbuild.common.function;

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.List.generateList;
import static org.smoothbuild.common.collect.List.nCopiesList;
import static org.smoothbuild.common.function.Function0.memoize;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;

public class Function0Test {
  @Nested
  class _memoize {
    @Test
    void calls_function_lazily() {
      Function0<String, IOException> function0 = mock();
      memoize(function0);
      verifyNoInteractions(function0);
    }

    @Test
    void apply_returns_value_from_wrapped_function() {
      var memoizer = memoize(() -> "abc");
      assertThat(memoizer.apply()).isEqualTo("abc");
    }

    @Test
    void second_call_to_apply_does_not_call_wrapped_function() throws Throwable {
      Function0<String, IOException> function0 = mock();
      var memoized = memoize(function0);
      memoized.apply();
      memoized.apply();

      verify(function0, times(1)).apply();
    }

    @Test
    void second_call_to_apply_returns_memoized_value() {
      var memoizer = memoize(() -> "abc");
      memoizer.apply();
      assertThat(memoizer.apply()).isEqualTo("abc");
    }

    @Test
    void multi_threaded_test() throws Exception {
      var atomicInteger = new AtomicInteger(7);
      var memoizer = memoize(atomicInteger::getAndIncrement);

      var count = 1000;
      var futures = invokeFunctionFromMultipleThreads(count, memoizer);
      assertThat(futures.map(Future::get)).isEqualTo(nCopiesList(count, 7));
    }

    private static List<Future<Integer>> invokeFunctionFromMultipleThreads(
        int count, Function0<Integer, RuntimeException> function0) {
      var countDownLatch = new CountDownLatch(1);
      try (var executorService = newVirtualThreadPerTaskExecutor()) {
        var futures = generateList(
            count, () -> executorService.submit(() -> awaitAndApply(function0, countDownLatch)));
        countDownLatch.countDown();
        return futures;
      }
    }

    private static Integer awaitAndApply(
        Function0<Integer, RuntimeException> function0, CountDownLatch countDownLatch)
        throws InterruptedException {
      countDownLatch.await();
      return function0.apply();
    }
  }
}
