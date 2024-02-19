package org.smoothbuild.common.function;

import static com.google.common.truth.Truth.assertThat;
import static java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.List.generateList;
import static org.smoothbuild.common.collect.List.nCopiesList;
import static org.smoothbuild.common.function.Function1.memoizer;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.List;

class Function1Test {
  @Nested
  class _memoize {
    @Test
    void calls_function_lazily() {
      Function1<Integer, String, IOException> function1 = mock();
      memoizer(function1);
      verifyNoInteractions(function1);
    }

    @Test
    void apply_returns_value_from_wrapped_function() {
      var memoizer = memoizer(Integer::toBinaryString);
      assertThat(memoizer.apply(7)).isEqualTo("111");
    }

    @Test
    void second_call_to_apply_does_not_call_wrapped_function() throws Throwable {
      Function1<Integer, String, IOException> function1 = mock();
      var memoized = memoizer(function1);
      memoized.apply(7);
      memoized.apply(7);

      verify(function1, times(1)).apply(7);
    }

    @Test
    void second_call_to_apply_returns_memoized_value() {
      var memoizer = memoizer(Integer::toBinaryString);
      memoizer.apply(7);
      assertThat(memoizer.apply(7)).isEqualTo("111");
    }
  }

  @Test
  void multi_threaded_test() throws Exception {
    var count = 1000;
    var listSize = 10;

    var counters = generateList(listSize, () -> new AtomicInteger());
    var memoizer = memoizer((Integer i) -> i.toString() + "=" + counters.get(i).getAndIncrement());

    var futures = invokeFunctionFromMultipleThreads(count, 10, memoizer);
    var actual = futures.map(Future::get).sortUsing(String::compareTo);
    var expected = generateList(listSize, (Integer i) -> i.toString() + "=0")
        .flatMap(s -> nCopiesList(count / listSize, s));
    assertThat(actual).isEqualTo(expected);
  }

  private static List<Future<String>> invokeFunctionFromMultipleThreads(
      int count, int listSize, Function1<Integer, String, RuntimeException> function1) {
    var countDownLatch = new CountDownLatch(1);
    try (var executorService = newVirtualThreadPerTaskExecutor()) {
      var futures = generateList(
          count,
          (i) ->
              executorService.submit(() -> awaitAndApply(function1, i % listSize, countDownLatch)));
      countDownLatch.countDown();
      return futures;
    }
  }

  private static String awaitAndApply(
      Function1<Integer, String, RuntimeException> function1,
      Integer i,
      CountDownLatch countDownLatch)
      throws InterruptedException {
    countDownLatch.await();
    return function1.apply(i);
  }
}
