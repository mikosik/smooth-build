package org.smoothbuild.common.concurrent;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;

public class ImmutablePromiseTest {
  @ParameterizedTest
  @MethodSource("constructorValues")
  void get_returns_value(String value) {
    var promise = new ImmutablePromise<>(value);
    assertThat(promise.get()).isEqualTo(value);
  }

  @ParameterizedTest
  @MethodSource("constructorValues")
  void toMaybe_returns_value(String value) {
    var promise = new ImmutablePromise<>(value);
    assertThat(promise.toMaybe()).isEqualTo(some(value));
  }

  @ParameterizedTest
  @MethodSource("constructorValues")
  void getBlocking_returns_value(String value) throws Exception {
    var promise = new ImmutablePromise<>(value);
    assertThat(promise.getBlocking()).isEqualTo(value);
  }

  static List<String> constructorValues() {
    return list("abc", null);
  }

  @Test
  void consumer_is_called_inside_add_consumer() {
    var promise = new ImmutablePromise<>("abc");
    Consumer<String> consumer = mock();
    promise.addConsumer(consumer);
    verify(consumer).accept("abc");
  }

  @Test
  void adding_null_consumer_fails() {
    var promise = new ImmutablePromise<>("abc");
    assertCall(() -> promise.addConsumer(null)).throwsException(NullPointerException.class);
  }
}
