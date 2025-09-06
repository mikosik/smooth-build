package org.smoothbuild.common.concurrent;

import static com.google.common.truth.Truth.assertThat;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ImmutablePromiseTest {
  @ParameterizedTest
  @MethodSource("constructorValues")
  void get_returns_value(String value) {
    var promise = new ImmutablePromise<>(value);
    assertThat(promise.get()).isEqualTo(value);
  }

  @Test
  void toMaybe_returns_value() {
    var promise = new ImmutablePromise<>("abc");
    assertThat(promise.toMaybe()).isEqualTo(some("abc"));
  }

  @ParameterizedTest
  @MethodSource("constructorValues")
  void getBlocking_returns_value(String value) {
    var promise = new ImmutablePromise<>(value);
    assertThat(promise.getBlocking()).isEqualTo(value);
  }

  static List<String> constructorValues() {
    return asList("abc", null);
  }

  @Test
  void consumer_is_called_inside_add_consumer() {
    var promise = new ImmutablePromise<>("abc");
    Consumer<String> consumer = mock();
    promise.addConsumer(consumer);
    verify(consumer).accept("abc");
  }

  @Test
  @SuppressWarnings("NullAway")
  void adding_null_consumer_fails() {
    var promise = new ImmutablePromise<>("abc");
    assertCall(() -> promise.addConsumer(null)).throwsException(NullPointerException.class);
  }
}
