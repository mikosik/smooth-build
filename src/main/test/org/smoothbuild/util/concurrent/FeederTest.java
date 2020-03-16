package org.smoothbuild.util.concurrent;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.smoothbuild.util.Strings.unlines;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

public class FeederTest {
  private Feeder<String> feeder;

  @Before
  public void before() {
    this.feeder = new Feeder<>();
  }

  @Test
  public void initially_value_is_null() {
    assertThat(feeder.value())
        .isNull();
  }

  @Test
  public void setting_value_to_null_causes_exception() {
    assertThrows(NullPointerException.class, () -> feeder.accept(null));
  }

  @Test
  public void adding_null_consumer_causes_exception() {
    assertThrows(NullPointerException.class, () -> feeder.addConsumer(null));
  }

  @Test
  public void value_returns_instance_passed_to_consume() {
    String value = "abc";
    feeder.accept(value);
    assertThat(feeder.value())
        .isSameInstanceAs(value);
  }

  @Test
  public void setting_value_twice_causes_exception() {
    feeder.accept("abc");
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> feeder.accept("def"));
    assertThat(e.getMessage())
        .isEqualTo(unlines(
            "Cannot set 'value' to: def",
            "as it is already set to: abc"
        ));
  }

  @Test
  public void added_consumer_is_not_called_when_no_value_is_set() {
    Consumer<String> consumer = consumerMock();
    feeder.addConsumer(consumer);
    verifyZeroInteractions(consumer);
  }

  @Test
  public void consumer_is_called_during_adding_when_value_is_already_set() {
    String value = "abc";
    feeder.accept(value);
    Consumer<String> consumer = consumerMock();
    feeder.addConsumer(consumer);
    verify(consumer).accept(value);
  }

  @Test
  public void consumer_is_called_when_value_is_added() {
    String value = "abc";
    Consumer<String> consumer = consumerMock();
    feeder.addConsumer(consumer);
    feeder.accept(value);
    verify(consumer).accept(value);
  }

  @Test
  public void accept_does_not_call_consumers_with_lock_held() {
    AtomicReference<Boolean> hadLock = new AtomicReference<>();
    feeder.addConsumer((value) -> {
      hadLock.set(feeder.isCurrentThreadHoldingALock());
    });
    feeder.accept("abc");

    assertThat(hadLock.get())
        .isFalse();
  }

  @Test
  public void add_consumer_does_not_call_consumer_with_lock_held() {
    feeder.accept("abc");
    feeder.addConsumer((value) -> {
      assertThat(feeder.isCurrentThreadHoldingALock())
          .isFalse();
    });
  }

  private static Consumer<String> consumerMock() {
    return mock(Consumer.class);
  }
}
