package org.smoothbuild.common.concurrent;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.truth.Truth.assertThat;
import static java.util.Collections.synchronizedSet;

import com.google.common.collect.ImmutableSet;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

public class AtomicBigIntegerTest {
  @Test
  public void incrementAndGet_returns_consecutive_integers() {
    var atomicBigInteger = new AtomicBigInteger();
    assertThat(atomicBigInteger.incrementAndGet()).isEqualTo(BigInteger.valueOf(1));
    assertThat(atomicBigInteger.incrementAndGet()).isEqualTo(BigInteger.valueOf(2));
    assertThat(atomicBigInteger.incrementAndGet()).isEqualTo(BigInteger.valueOf(3));
    assertThat(atomicBigInteger.incrementAndGet()).isEqualTo(BigInteger.valueOf(4));
  }

  @Test
  public void concurrent_calls_to_incrementAndGet_returns_mutually_different_values() {
    int numbersPerThread = 5000;
    int threadsCount = 4;

    var atomicBigInteger = new AtomicBigInteger();
    var startThreadLatch = new CountDownLatch(1);
    var allNumbers = synchronizedSet(new HashSet<BigInteger>());

    var threads = new ArrayList<Thread>();
    for (int i = 0; i < threadsCount; i++) {
      var thread = new Thread(
          () -> fetchNumbers(startThreadLatch, atomicBigInteger, allNumbers, numbersPerThread));
      threads.add(thread);
      thread.start();
    }
    startThreadLatch.countDown();

    waitForThreadsCompletion(threads);
    var expectedSet = setOfIntegersFromOneTo(threadsCount * numbersPerThread);
    assertThat(allNumbers).containsExactlyElementsIn(expectedSet);
  }

  private static ImmutableSet<BigInteger> setOfIntegersFromOneTo(int max) {
    return IntStream.rangeClosed(1, max).mapToObj(BigInteger::valueOf).collect(toImmutableSet());
  }

  private static void fetchNumbers(
      CountDownLatch startThreadLatch,
      AtomicBigInteger atomicBigInteger,
      Set<BigInteger> allNumbers,
      int size) {
    try {
      startThreadLatch.await();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    var thisThreadNumbers = new HashSet<BigInteger>();
    for (int j = 0; j < size; j++) {
      thisThreadNumbers.add(atomicBigInteger.incrementAndGet());
    }
    allNumbers.addAll(thisThreadNumbers);
  }

  private static void waitForThreadsCompletion(List<Thread> threads) {
    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
