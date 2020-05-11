package org.smoothbuild.util.concurrent;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.util.Strings.unlines;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings("ClassCanBeStatic")
public class FeedingConsumerTest {
  private FeedingConsumer<String> feedingConsumer;

  @BeforeEach
  public void before() {
    this.feedingConsumer = new FeedingConsumer<>();
  }

  @Test
  public void initially_value_is_null() {
    assertThat(feedingConsumer.get())
        .isNull();
  }

  @Test
  public void setting_value_to_null_causes_exception() {
    assertThrows(NullPointerException.class, () -> feedingConsumer.accept(null));
  }

  @Test
  public void adding_null_consumer_causes_exception() {
    assertThrows(NullPointerException.class, () -> feedingConsumer.addConsumer(null));
  }

  @Test
  public void value_returns_instance_passed_to_consume() {
    String value = "abc";
    feedingConsumer.accept(value);
    assertThat(feedingConsumer.get())
        .isSameInstanceAs(value);
  }

  @Test
  public void setting_value_twice_causes_exception() {
    feedingConsumer.accept("abc");
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> feedingConsumer.accept("def"));
    assertThat(e.getMessage())
        .isEqualTo(unlines(
            "Cannot set 'value' to: def",
            "as it is already set to: abc"
        ));
  }

  @Test
  public void added_consumer_is_not_called_when_no_value_is_set() {
    Consumer<String> consumer = stringConsumer();
    feedingConsumer.addConsumer(consumer);
    verifyNoInteractions(consumer);
  }

  @Test
  public void consumer_is_called_during_adding_when_value_is_already_set() {
    String value = "abc";
    feedingConsumer.accept(value);
    Consumer<String> consumer = stringConsumer();
    feedingConsumer.addConsumer(consumer);
    verify(consumer).accept(value);
  }

  @Test
  public void consumer_is_called_when_value_is_added() {
    String value = "abc";
    Consumer<String> consumer = stringConsumer();
    feedingConsumer.addConsumer(consumer);
    feedingConsumer.accept(value);
    verify(consumer).accept(value);
  }

  @Test
  public void accept_does_not_call_consumers_with_lock_held() {
    AtomicReference<Boolean> hadLock = new AtomicReference<>();
    feedingConsumer.addConsumer((value) -> {
      hadLock.set(feedingConsumer.isCurrentThreadHoldingALock());
    });
    feedingConsumer.accept("abc");

    assertThat(hadLock.get())
        .isFalse();
  }

  @Test
  public void add_consumer_does_not_call_consumer_with_lock_held() {
    feedingConsumer.accept("abc");
    feedingConsumer.addConsumer((value) -> {
      assertThat(feedingConsumer.isCurrentThreadHoldingALock())
          .isFalse();
    });
  }

  @Nested
  class chained {
    @Test
    void value_is_initially_null() {
      Feeder<Integer> chained = feedingConsumer.chain(String::length);
      assertThat(chained.get())
          .isNull();
    }

    @Test
    public void adding_null_consumer_causes_exception() {
      Feeder<Integer> chained = feedingConsumer.chain(String::length);
      assertThrows(NullPointerException.class, () -> chained.addConsumer(null));
    }

    @Test
    void value_returns_converted_value() {
      Feeder<Integer> chained = feedingConsumer.chain(String::length);
      feedingConsumer.accept("12345");
      assertThat(chained.get())
          .isEqualTo(5);
    }

    @Test
    public void added_consumer_is_not_called_when_no_value_is_set() {
      Feeder<Integer> chained = feedingConsumer.chain(String::length);
      Consumer<Integer> consumer = intConsumer();
      chained.addConsumer(consumer);
      verifyNoInteractions(consumer);
    }

    @Test
    public void consumer_is_called_during_adding_when_value_is_already_set() {
      Feeder<Integer> chained = feedingConsumer.chain(String::length);
      feedingConsumer.accept("12345");
      Consumer<Integer> consumer = intConsumer();
      chained.addConsumer(consumer);
      verify(consumer).accept(5);
    }

    @Test
    public void consumer_is_called_when_value_is_added() {
      Feeder<Integer> chained = feedingConsumer.chain(String::length);
      Consumer<Integer> consumer = intConsumer();
      chained.addConsumer(consumer);
      feedingConsumer.accept("12345");
      verify(consumer).accept(5);
    }

    @Test
    public void accept_does_not_call_consumers_with_lock_held() {
      FeedingConsumer<Integer> chained =
          (FeedingConsumer<Integer>) feedingConsumer.chain(String::length);
      AtomicReference<Boolean> hadLock = new AtomicReference<>();
      chained.addConsumer((value) -> {
        hadLock.set(chained.isCurrentThreadHoldingALock());
      });
      feedingConsumer.accept("abc");

      assertThat(hadLock.get())
          .isFalse();
    }

    @Test
    public void add_consumer_does_not_call_consumer_with_lock_held() {
      FeedingConsumer<Integer> chained =
          (FeedingConsumer<Integer>) feedingConsumer.chain(String::length);
      feedingConsumer.accept("abc");
      chained.addConsumer((value) -> {
        assertThat(chained.isCurrentThreadHoldingALock())
            .isFalse();
      });
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
