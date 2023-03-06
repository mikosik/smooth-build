package org.smoothbuild.util.concurrent;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Strings.unlines;

import java.util.function.Consumer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class PromisedValueTest {
  @Test
  public void default_construct_set_values_to_null() {
    var promisedValue = new PromisedValue<>();
    assertThat(promisedValue.get())
        .isNull();
  }

  @Test
  public void initializing_constructor_set_value_to_constructor_arg() {
    var promisedValue = new PromisedValue<>("abc");
    assertThat(promisedValue.get())
        .isEqualTo("abc");
  }

  @Test
  public void setting_value_to_null_causes_exception() {
    var promisedValue = new PromisedValue<>();
    assertCall(() -> promisedValue.accept(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void adding_null_consumer_causes_exception() {
    var promisedValue = new PromisedValue<>();
    assertCall(() -> promisedValue.addConsumer(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void value_returns_instance_passed_to_consume() {
    var promisedValue = new PromisedValue<>();
    String value = "abc";
    promisedValue.accept(value);
    assertThat(promisedValue.get())
        .isSameInstanceAs(value);
  }

  @Test
  public void value_returns_instance_passed_to_initializing_constructor() {
    var promisedValue = new PromisedValue<>("abc");
    assertThat(promisedValue.get())
        .isSameInstanceAs("abc");
  }

  @Test
  public void setting_value_after_initializing_constructor_was_used_causes_exception() {
    var promisedValue = new PromisedValue<>("abc");
    assertCall(() -> promisedValue.accept("def"))
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void setting_value_twice_causes_exception() {
    var promisedValue = new PromisedValue<>();
    promisedValue.accept("abc");
    assertCall(() -> promisedValue.accept("def"))
        .throwsException(new IllegalStateException(unlines(
            "Cannot set 'value' to: def",
            "as it is already set to: abc"
        )));
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
    void value_is_initially_null() {
      var promisedValue = new PromisedValue<String>();
      Promise<Integer> chained = promisedValue.chain(String::length);
      assertThat(chained.get())
          .isNull();
    }

    @Test
    public void adding_null_consumer_causes_exception() {
      var promisedValue = new PromisedValue<String>();
      Promise<Integer> chained = promisedValue.chain(String::length);
      assertCall(() -> chained.addConsumer(null))
          .throwsException(NullPointerException.class);
    }

    @Test
    void value_returns_converted_value() {
      var promisedValue = new PromisedValue<String>();
      Promise<Integer> chained = promisedValue.chain(String::length);
      promisedValue.accept("12345");
      assertThat(chained.get())
          .isEqualTo(5);
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
      PromisedValue<Integer> chained =
          (PromisedValue<Integer>) promisedValue.chain(String::length);
      chained.addConsumer((value) -> assertThat(chained.isCurrentThreadHoldingALock()).isFalse());
      promisedValue.accept("abc");
    }

    @Test
    public void add_consumer_does_not_call_consumer_with_lock_held() {
      var promisedValue = new PromisedValue<String>();
      PromisedValue<Integer> chained =
          (PromisedValue<Integer>) promisedValue.chain(String::length);
      promisedValue.accept("abc");
      chained.addConsumer((value) -> assertThat(chained.isCurrentThreadHoldingALock()).isFalse());
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
