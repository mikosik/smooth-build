package org.smoothbuild.common.function;

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.List.generateList;
import static org.smoothbuild.common.function.Function0.memoize;

import java.util.concurrent.Callable;
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
      var function0 = mock(Function0.class);
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
      var function0 = mock(Function0.class);
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
      var futures = invokeMemoizingFunctionFromMultipleThreads(count, memoizer);
      assertThat(futures.map(Future::get)).isEqualTo(generateList(count, () -> 7));
    }

    private static List<Future<Integer>> invokeMemoizingFunctionFromMultipleThreads(
        int count, Function0<Integer, RuntimeException> memoizingFunction) {
      var countDownLatch = new CountDownLatch(1);
      try (var executorService = newVirtualThreadPerTaskExecutor()) {
        var futures = generateList(count, () -> (Callable<Integer>) () -> {
              countDownLatch.await();
              return memoizingFunction.apply();
            })
            .map(executorService::submit);
        countDownLatch.countDown();
        return futures;
      }
    }
  }
}
