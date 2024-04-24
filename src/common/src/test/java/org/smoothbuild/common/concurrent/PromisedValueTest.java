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

public class PromisedValueTest {
  @Test
  public void default_construct_set_value_to_none() {
    var promisedValue = new PromisedValue<>();
    assertThat(promisedValue.toMaybe()).isEqualTo(none());
    assertCall(promisedValue::get).throwsException(NoSuchElementException.class);
  }

  @Test
  public void initializing_constructor_set_value_to_constructor_arg() {
    var promisedValue = new PromisedValue<>("abc");
    assertThat(promisedValue.get()).isEqualTo("abc");
  }

  @Test
  public void setting_value_to_null_is_allowed() {
    var promisedValue = new PromisedValue<>();
    promisedValue.accept(null);
    assertThat(promisedValue.get()).isNull();
  }

  @Test
  public void adding_null_consumer_fails() {
    var promisedValue = new PromisedValue<>();
    assertCall(() -> promisedValue.addConsumer(null)).throwsException(NullPointerException.class);
  }

  @Nested
  class _get {
    @Test
    public void returns_instance_passed_to_consume() {
      var promisedValue = new PromisedValue<>();
      var value = "abc";
      promisedValue.accept(value);
      assertThat(promisedValue.get()).isSameInstanceAs(value);
      assertThat(promisedValue.toMaybe()).isEqualTo(some(value));
    }

    @Test
    public void returns_instance_passed_to_initializing_constructor() {
      var value = "abc";
      var promisedValue = new PromisedValue<>(value);
      assertThat(promisedValue.get()).isSameInstanceAs(value);
      assertThat(promisedValue.toMaybe()).isEqualTo(some(value));
    }
  }

  @Nested
  class _getBlocking {
    @Test
    public void returns_instance_passed_to_consume() throws Exception {
      var promisedValue = new PromisedValue<>();
      promisedValue.accept("abc");
      assertThat(promisedValue.getBlocking()).isSameInstanceAs("abc");
    }

    @Test
    public void returns_instance_passed_to_initializing_constructor() throws Exception {
      var promisedValue = new PromisedValue<>("abc");
      assertThat(promisedValue.getBlocking()).isSameInstanceAs("abc");
    }

    @Test
    public void waits_until_value_is_set() throws Exception {
      var promisedValue = new PromisedValue<>();
      startVirtualThread(() -> {
        sleepMillis(100);
        promisedValue.accept("abc");
      });
      assertThat(promisedValue.getBlocking()).isSameInstanceAs("abc");
    }
  }

  @Test
  public void setting_value_after_initializing_constructor_was_used_fails() {
    var promisedValue = new PromisedValue<>("abc");
    assertCall(() -> promisedValue.accept("def")).throwsException(IllegalStateException.class);
  }

  @Test
  public void setting_value_twice_fails() {
    var promisedValue = new PromisedValue<>();
    promisedValue.accept("abc");
    assertCall(() -> promisedValue.accept("def"))
        .throwsException(new IllegalStateException(
            unlines("Cannot set 'value' to: def", "as it is already set to: abc")));
  }

  @Test
  public void added_consumer_is_not_called_when_no_value_is_set() {
    var promisedValue = new PromisedValue<String>();
    Consumer<String> consumer = stringConsumer();
    promisedValue.addConsumer(consumer);
    verifyNoInteractions(consumer);
  }

  @Test
  public void consumer_is_called_during_adding_when_value_is_already_set() {
    var promisedValue = new PromisedValue<String>();
    String value = "abc";
    promisedValue.accept(value);
    Consumer<String> consumer = stringConsumer();
    promisedValue.addConsumer(consumer);
    verify(consumer).accept(value);
  }

  @Test
  public void consumer_is_called_during_adding_when_value_was_set_in_initializing_constructor() {
    var promisedValue = new PromisedValue<>("abc");
    Consumer<String> consumer = stringConsumer();
    promisedValue.addConsumer(consumer);
    verify(consumer).accept("abc");
  }

  @Test
  public void consumer_is_called_when_value_is_added() {
    var promisedValue = new PromisedValue<String>();
    String value = "abc";
    Consumer<String> consumer = stringConsumer();
    promisedValue.addConsumer(consumer);
    promisedValue.accept(value);
    verify(consumer).accept(value);
  }

  @Test
  public void accept_does_not_call_consumers_with_lock_held() {
    var promisedValue = new PromisedValue<String>();
    promisedValue.addConsumer(
        (value) -> assertThat(promisedValue.isCurrentThreadHoldingALock()).isFalse());
    promisedValue.accept("abc");
  }

  @Test
  public void add_consumer_does_not_call_consumer_with_lock_held() {
    var promisedValue = new PromisedValue<String>();
    promisedValue.accept("abc");
    promisedValue.addConsumer(
        (value) -> assertThat(promisedValue.isCurrentThreadHoldingALock()).isFalse());
  }

  @Nested
  class chained {
    @Test
    void value_is_initially_none() {
      var promisedValue = new PromisedValue<String>();
      Promise<Integer> chained = promisedValue.chain(String::length);
      assertCall(chained::get).throwsException(NoSuchElementException.class);
    }

    @Test
    public void adding_null_consumer_fails() {
      var promisedValue = new PromisedValue<String>();
      Promise<Integer> chained = promisedValue.chain(String::length);
      assertCall(() -> chained.addConsumer(null)).throwsException(NullPointerException.class);
    }

    @Test
    void get_returns_converted_value() {
      var promisedValue = new PromisedValue<String>();
      Promise<Integer> chained = promisedValue.chain(String::length);
      promisedValue.accept("12345");
      assertThat(chained.get()).isEqualTo(5);
    }

    @Test
    public void added_consumer_is_not_called_when_no_value_is_set() {
      var promisedValue = new PromisedValue<String>();
      Promise<Integer> chained = promisedValue.chain(String::length);
      Consumer<Integer> consumer = intConsumer();
      chained.addConsumer(consumer);
      verifyNoInteractions(consumer);
    }

    @Test
    public void consumer_is_called_during_adding_when_value_is_already_set() {
      var promisedValue = new PromisedValue<String>();
      Promise<Integer> chained = promisedValue.chain(String::length);
      promisedValue.accept("12345");
      Consumer<Integer> consumer = intConsumer();
      chained.addConsumer(consumer);
      verify(consumer).accept(5);
    }

    @Test
    public void consumer_is_called_when_value_is_added() {
      var promisedValue = new PromisedValue<String>();
      Promise<Integer> chained = promisedValue.chain(String::length);
      Consumer<Integer> consumer = intConsumer();
      chained.addConsumer(consumer);
      promisedValue.accept("12345");
      verify(consumer).accept(5);
    }

    @Test
    public void accept_does_not_call_consumers_with_lock_held() {
      var promisedValue = new PromisedValue<String>();
      PromisedValue<Integer> chained = (PromisedValue<Integer>) promisedValue.chain(String::length);
      chained.addConsumer(
          (value) -> assertThat(chained.isCurrentThreadHoldingALock()).isFalse());
      promisedValue.accept("abc");
    }

    @Test
    public void add_consumer_does_not_call_consumer_with_lock_held() {
      var promisedValue = new PromisedValue<String>();
      PromisedValue<Integer> chained = (PromisedValue<Integer>) promisedValue.chain(String::length);
      promisedValue.accept("abc");
      chained.addConsumer(
          (value) -> assertThat(chained.isCurrentThreadHoldingALock()).isFalse());
    }
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
