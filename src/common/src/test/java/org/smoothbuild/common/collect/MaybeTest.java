package org.smoothbuild.common.collect;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.collect.Maybe.None;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.function.Function2;

public class MaybeTest {
  @Nested
  class _maybe {
    @Test
    void with_null_argument_creates_none() {
      assertThat(maybe(null)).isEqualTo(none());
    }

    @Test
    void with_non_null_argument_creates_some() {
      assertThat(maybe("a")).isEqualTo(some("a"));
    }
  }

  @Nested
  class _some {
    @Test
    void can_hold_null() {
      var some = some(null);
      assertThat(some.get()).isNull();
    }

    @Test
    void get_returns_held_element() {
      var some = some("a");
      assertThat(some.get()).isEqualTo("a");
    }

    @Test
    void getOr_returns_element() {
      var some = some("a");
      assertThat(some.getOr("b")).isEqualTo("a");
    }

    @Test
    void getOrGet_returns_element() {
      var some = some("a");
      assertThat(some.getOrGet(() -> "b")).isEqualTo("a");
    }

    @Test
    void getOrGet_not_calls_supplier() {
      var some = some("a");
      assertDoesNotThrow(() -> some.getOrGet(() -> {
        throw new RuntimeException();
      }));
    }

    @Test
    void getOrThrow_returns_element() {
      var some = some("a");
      assertThat(some.getOrThrow(() -> new RuntimeException())).isEqualTo("a");
    }

    @Test
    void ifPresent_calls_consumer() {
      Consumer1<String, ? extends RuntimeException> consumer = mock();
      some("a").ifPresent(consumer);
      verify(consumer).accept("a");
    }

    @Test
    void ifPresent_returns_this() {
      var some = some("a");
      assertThat(some.ifPresent(x -> {})).isSameInstanceAs(some);
    }

    @Test
    void is_some_returns_true() {
      var some = some("a");
      assertThat(some.isSome()).isTrue();
    }

    @Test
    void is_none_returns_false() {
      var some = some("a");
      assertThat(some.isNone()).isFalse();
    }

    @Test
    void map_converts_element() {
      var some = some(16);
      assertThat(some.map(Integer::toHexString)).isEqualTo(some("10"));
    }

    @Test
    void map_can_convert_to_null() {
      var some = some(16);
      assertThat(some.map(x -> (String) null)).isEqualTo(some(null));
    }

    @Test
    void map_propagates_exception() {
      var some = some("a");
      var exception = new Exception("message");
      assertCall(() -> some.map(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void flatMap_converting_to_some() {
      var some = some(16);
      assertThat(some.flatMap(i -> some(Integer.toHexString(i)))).isEqualTo(some("10"));
    }

    @Test
    void flatMap_converting_to_none() {
      var some = some("a");
      assertThat(some.flatMap(i -> none())).isEqualTo(none());
    }

    @Test
    void flatMap_converting_to_null_fails() {
      var some = some("a");
      assertCall(() -> some.flatMap(i -> null)).throwsException(NullPointerException.class);
    }

    @Test
    void flatMap_propagates_exception() {
      var some = some("a");
      var exception = new Exception("message");
      assertCall(() -> some.flatMap(s -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void toList_returns_single_element_list() {
      assertThat(some("a").toList()).isEqualTo(list("a"));
    }

    @Test
    void to_string() {
      var some = some("abc");
      assertThat(some.toString()).isEqualTo("Some(abc)");
    }
  }

  @Nested
  class _none {
    @Test
    void get_fails() {
      var none = none();
      assertCall(() -> assertThat(none.get())).throwsException(NoSuchElementException.class);
    }

    @Test
    void getOr_returns_argument() {
      var none = none();
      assertThat(none.getOr("b")).isEqualTo("b");
    }

    @Test
    void getOrGet_returns_supplied_value() {
      var none = none();
      assertThat(none.getOrGet(() -> "b")).isEqualTo("b");
    }

    @Test
    void getOrGet_propagates_exception_from_supplier() {
      var none = none();
      var exception = new Exception("message");
      assertCall(() -> none.getOrGet(() -> {
            throw exception;
          }))
          .throwsException(exception);
    }

    @Test
    void getOrThrow_fails() {
      var none = none();
      var exception = new Exception("message");
      assertCall(() -> none.getOrThrow(() -> exception)).throwsException(exception);
    }

    @Test
    void ifPresent_not_calls_consumer() {
      Consumer1<Object, ? extends RuntimeException> consumer = mock();
      none().ifPresent(consumer);
      verifyNoInteractions(consumer);
    }

    @Test
    void ifPresent_returns_this() {
      var none = none();
      assertThat(none.ifPresent(x -> {})).isSameInstanceAs(none);
    }

    @Test
    void is_some_returns_false() {
      var none = none();
      assertThat(none.isSome()).isFalse();
    }

    @Test
    void is_none_returns_true() {
      var none = none();
      assertThat(none.isNone()).isTrue();
    }

    @Test
    void map_returns_none() {
      None<String> none = none();
      assertThat(none.map(String::toUpperCase)).isEqualTo(none());
    }

    @Test
    void map_not_calls_mapper() throws Exception {
      var none = none();
      assertThat(none.map(x -> {
            throw new Exception();
          }))
          .isEqualTo(none());
    }

    @Test
    void flatMap_returns_none() {
      None<String> none = none();
      assertThat(none.flatMap(Maybe::some)).isEqualTo(none());
    }

    @Test
    void flatMap_not_calls_mapper() throws Exception {
      var none = none();
      assertThat(none.map(x -> {
            throw new Exception();
          }))
          .isEqualTo(none());
    }

    @Test
    void toList_returns_single_element_list() {
      assertThat(none().toList()).isEqualTo(list());
    }

    @Test
    void to_string() {
      var none = none();
      assertThat(none.toString()).isEqualTo("None");
    }
  }

  @Nested
  class _map_pair {
    @Test
    void none_and_none() {
      Maybe<Boolean> bool = none();
      Maybe<Integer> int_ = none();
      assertThat(bool.mapWith(int_, concatBoolAndInt)).isEqualTo(none());
    }

    @Test
    void none_and_some() {
      Maybe<Boolean> bool = none();
      Maybe<Integer> int_ = some(1);
      assertThat(bool.mapWith(int_, concatBoolAndInt)).isEqualTo(none());
    }

    @Test
    void some_and_none() {
      Maybe<Boolean> bool = some(true);
      Maybe<Integer> int_ = none();
      assertThat(bool.mapWith(int_, concatBoolAndInt)).isEqualTo(none());
    }

    @Test
    void some_and_some() {
      Maybe<Boolean> bool = some(true);
      Maybe<Integer> int_ = some(7);
      assertThat(bool.mapWith(int_, concatBoolAndInt)).isEqualTo(some("true7"));
    }

    @Test
    void exception_from_function_is_propagated() {
      Maybe<Boolean> bool = some(true);
      Maybe<Integer> int_ = some(7);
      var exception = new Exception("message");

      assertCall(() -> bool.mapWith(int_, (b, i) -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Nested
  class _flat_map_pair {
    @Test
    void none_and_none() {
      Maybe<Boolean> bool = none();
      Maybe<Integer> int_ = none();
      assertThat(bool.flatMapWith(int_, concatBoolAndIntOpt)).isEqualTo(none());
    }

    @Test
    void none_and_some() {
      Maybe<Boolean> bool = none();
      Maybe<Integer> int_ = some(1);
      assertThat(bool.flatMapWith(int_, concatBoolAndIntOpt)).isEqualTo(none());
    }

    @Test
    void some_and_none() {
      Maybe<Boolean> bool = some(true);
      Maybe<Integer> int_ = none();
      assertThat(bool.flatMapWith(int_, concatBoolAndIntOpt)).isEqualTo(none());
    }

    @Test
    void some_and_some() {
      Maybe<Boolean> bool = some(true);
      Maybe<Integer> int_ = some(7);
      assertThat(bool.flatMapWith(int_, concatBoolAndIntOpt)).isEqualTo(some("true7"));
    }

    @Test
    void exception_from_function_is_propagated() {
      Maybe<Boolean> bool = some(true);
      Maybe<Integer> int_ = some(7);
      var exception = new Exception("message");

      assertCall(() -> bool.flatMapWith(int_, (b, i) -> {
            throw exception;
          }))
          .throwsException(exception);
    }
  }

  @Test
  void equals_and_hash_code() {
    new EqualsTester()
        .addEqualityGroup(some("a"), some("a"))
        .addEqualityGroup(some("b"), some("b"))
        .addEqualityGroup(none(), none());
  }

  private static final Function2<Boolean, Integer, String, RuntimeException> concatBoolAndInt =
      (Boolean a, Integer b) -> Boolean.toString(a) + b;
  private static final Function2<Boolean, Integer, Maybe<String>, RuntimeException>
      concatBoolAndIntOpt = (Boolean a, Integer b) -> some(Boolean.toString(a) + b);
}
