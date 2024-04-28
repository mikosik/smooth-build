package org.smoothbuild.common.concurrent;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.Thread.startVirtualThread;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.base.Strings.unlines;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.testing.TestingThread.sleepMillis;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.NoSuchElementException;
import java.util.function.Consumer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class MutablePromiseTest {
  @Test
  void default_construct_set_value_to_none() {
    var mutablePromise = new MutablePromise<>();
    assertThat(mutablePromise.toMaybe()).isEqualTo(none());
    assertCall(mutablePromise::get).throwsException(NoSuchElementException.class);
  }

  @Test
  void setting_value_to_null_is_allowed() {
    var mutablePromise = new MutablePromise<>();
    mutablePromise.accept(null);
    assertThat(mutablePromise.get()).isNull();
  }

  @Test
  void adding_null_consumer_fails() {
    var mutablePromise = new MutablePromise<>();
    assertCall(() -> mutablePromise.addConsumer(null)).throwsException(NullPointerException.class);
  }

  @Test
  void get_returns_instance_passed_to_consume() {
    var mutablePromise = new MutablePromise<>();
    var value = "abc";
    mutablePromise.accept(value);
    assertThat(mutablePromise.get()).isSameInstanceAs(value);
    assertThat(mutablePromise.toMaybe()).isEqualTo(some(value));
  }

  @Nested
  class _getBlocking {
    @Test
    void returns_instance_passed_to_accept() throws Exception {
      var mutablePromise = new MutablePromise<>();
      mutablePromise.accept("abc");
      assertThat(mutablePromise.getBlocking()).isSameInstanceAs("abc");
    }

    @Test
    void waits_until_value_is_set() throws Exception {
      var mutablePromise = new MutablePromise<>();
      startVirtualThread(() -> {
        sleepMillis(100);
        mutablePromise.accept("abc");
      });
      assertThat(mutablePromise.getBlocking()).isSameInstanceAs("abc");
    }
  }

  @Test
  void setting_value_twice_fails() {
    var mutablePromise = new MutablePromise<>();
    mutablePromise.accept("abc");
    assertCall(() -> mutablePromise.accept("def"))
        .throwsException(new IllegalStateException(
            unlines("Cannot set 'value' to: def", "as it is already set to: abc")));
  }

  @Test
  void added_consumer_is_not_called_when_no_value_is_set() {
    var mutablePromise = new MutablePromise<String>();
    Consumer<String> consumer = stringConsumer();
    mutablePromise.addConsumer(consumer);
    verifyNoInteractions(consumer);
  }

  @Test
  void consumer_is_called_during_adding_when_value_is_already_set() {
    var mutablePromise = new MutablePromise<String>();
    String value = "abc";
    mutablePromise.accept(value);
    Consumer<String> consumer = stringConsumer();
    mutablePromise.addConsumer(consumer);
    verify(consumer).accept(value);
  }

  @Test
  void consumer_is_called_when_value_is_added() {
    var mutablePromise = new MutablePromise<String>();
    String value = "abc";
    Consumer<String> consumer = stringConsumer();
    mutablePromise.addConsumer(consumer);
    mutablePromise.accept(value);
    verify(consumer).accept(value);
  }

  @Test
  void accept_does_not_call_consumers_with_lock_held() {
    var mutablePromise = new MutablePromise<String>();
    mutablePromise.addConsumer(
        (value) -> assertThat(mutablePromise.isCurrentThreadHoldingALock()).isFalse());
    mutablePromise.accept("abc");
  }

  @Test
  void add_consumer_does_not_call_consumer_with_lock_held() {
    var mutablePromise = new MutablePromise<String>();
    mutablePromise.accept("abc");
    mutablePromise.addConsumer(
        (value) -> assertThat(mutablePromise.isCurrentThreadHoldingALock()).isFalse());
  }

  @SuppressWarnings("unchecked")
  private static Consumer<String> stringConsumer() {
    return mock(Consumer.class);
  }

  @SuppressWarnings("unchecked")
  private static Consumer<Integer> intConsumer() {
    return mock(Consumer.class);
  }
}
