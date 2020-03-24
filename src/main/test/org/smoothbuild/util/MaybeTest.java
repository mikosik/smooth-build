package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.errors;
import static org.smoothbuild.util.Maybe.pullUp;
import static org.smoothbuild.util.Maybe.value;

import org.junit.jupiter.api.Test;

public class MaybeTest {
  @Test
  public void created_value_has_value() {
    Maybe<String> maybe = value("abc");
    assertThat(maybe.hasValue())
        .isTrue();
  }

  @Test
  public void created_value_has_no_error() {
    Maybe<String> maybe = value("abc");
    assertThat(maybe.errors())
        .isEmpty();
  }

  @Test
  public void created_value_contains_value() {
    Maybe<String> maybe = value("abc");
    assertThat(maybe.value())
        .isEqualTo("abc");
  }

  @Test
  public void created_error_contains_error() {
    Maybe<String> maybe = error(13);
    assertThat(maybe.errors())
        .containsExactly(13);
  }

  @Test
  public void errors_static_factory_accepts_list_of_errors() {
    Maybe<String> maybe = errors(list(13, 14));
    assertThat(maybe.errors())
        .containsExactly(13, 14)
        .inOrder();
  }

  @Test
  public void created_error_fails_for_null() {
    assertCall(() -> error(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void created_error_has_no_value() {
    Maybe<String> maybe = error(13);
    assertThat(maybe.hasValue())
        .isFalse();
  }

  @Test
  public void created_error_fails_for_value() {
    Maybe<String> maybe = error(13);
    assertCall(maybe::value)
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void created_errors_contains_error() {
    Maybe<String> maybe = errors(list(13, 14));
    assertThat(maybe.errors())
        .containsExactly(13, 14)
        .inOrder();
  }

  @Test
  public void created_errors_fails_for_null() {
    assertCall(() -> errors(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void created_errors_fails_for_empty_list() {
    assertCall(() -> errors(list()))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void created_errors_has_no_value() {
    Maybe<String> maybe = errors(list(13, 14));
    assertThat(maybe.hasValue())
        .isFalse();
  }

  @Test
  public void created_errors_fails_for_value() {
    Maybe<String> maybe = errors(list(13, 14));
    assertCall(maybe::value)
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void created_maybe_with_null_value_and_no_errors_has_null_value() {
    Maybe<String> maybe = Maybe.maybe(null, list());
    assertThat(maybe.value())
        .isNull();
  }

  @Test
  public void created_maybe_with_value_and_no_errors_has_value() {
    Maybe<String> maybe = Maybe.maybe("value", list());
    assertThat(maybe.value())
        .isEqualTo("value");
  }

  @Test
  public void created_maybe_with_value_and_no_errors_has_no_errors() {
    Maybe<String> maybe = Maybe.maybe("value", list());
    assertThat(maybe.errors())
        .isEmpty();
  }

  @Test
  public void created_maybe_with_null_value_and_errors_fails_for_value() {
    Maybe<String> maybe = Maybe.maybe(null, list(13, 14));
    assertCall(maybe::value)
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void created_maybe_with_null_value_and_errors_has_errors() {
    Maybe<String> maybe = Maybe.maybe(null, list(13, 14));
    assertThat(maybe.errors())
        .containsExactly(13, 14)
        .inOrder();
  }

  @Test
  public void created_maybe_with_value_and_errors_fails_for_value() {
    Maybe<String> maybe = Maybe.maybe("value", list(13, 14));
    assertCall(maybe::value)
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void created_maybe_with_value_and_errors_has_errors() {
    Maybe<String> maybe = Maybe.maybe("value", list(13, 14));
    assertThat(maybe.errors())
        .containsExactly(13, 14)
        .inOrder();
  }

  @Test
  public void value_addError_has_no_value() {
    Maybe<String> maybe = value("value").addError(13);
    assertThat(maybe.hasValue())
        .isFalse();
  }

  @Test
  public void value_addErrors_has_no_value() {
    Maybe<String> maybe = value("value").addErrors(list(13, 14));
    assertThat(maybe.hasValue())
        .isFalse();
  }

  @Test
  public void value_addErrors_empty_list_has_value() {
    Maybe<String> maybe = value("value").addErrors(list());
    assertThat(maybe.hasValue())
        .isTrue();
  }

  @Test
  public void error_addError_has_both() {
    Maybe<Object> maybe = error(13).addError(14);
    assertThat(maybe.errors())
        .containsExactly(13, 14)
        .inOrder();
  }

  @Test
  public void error_addErrors_has_all_errors() {
    Maybe<Object> maybe = error(13).addErrors(list(14, 15));
    assertThat(maybe.errors())
        .containsExactly(13, 14, 15)
        .inOrder();
  }

  @Test
  public void error_addErrors_empty_list_has_initial_error() {
    Maybe<Object> maybe = error(13).addErrors(list());
    assertThat(maybe.errors())
        .containsExactly(13);
  }

  @Test
  public void maybes_with_equal_values_are_equal() {
    assertThat(value("abc"))
        .isEqualTo(value("abc"));
  }

  @Test
  public void maybes_with_equal_errors_are_equal() {
    assertThat(error(13))
        .isEqualTo(error(13));
  }

  @Test
  public void maybe_value_is_not_equal_to_maybe_error() {
    assertThat(value("abc"))
        .isNotEqualTo(error(13));
  }

  @Test
  public void error_has_different_hashcode_than_value() {
    assertThat(value("abc").hashCode())
        .isNotEqualTo(error("abc").hashCode());
  }

  @Test
  public void to_string_value() {
    assertThat(value("abc").toString())
        .isEqualTo("Maybe.value(abc)");
  }

  @Test
  public void to_string_error() {
    assertThat(error("abc").toString())
        .isEqualTo("Maybe.error(abc)");
  }

  @Test
  public void pulling_up_empty_list_gives_value_with_empty_list() {
    assertThat(pullUp(list()))
        .isEqualTo(value(list()));
  }

  @Test
  public void pulling_up_list_with_value_returns_value_with_one_element() {
    assertThat(pullUp(list(value("abc"))))
        .isEqualTo(value(list("abc")));
  }

  @Test
  public void pulling_up_list_with_error_returns_error() {
    assertThat(pullUp(list(error(13))))
        .isEqualTo(error(13));
  }

  @Test
  public void pulling_up_list_with_value_and_error_returns_error() {
    assertThat(pullUp(list(value("abc"), error(13))))
        .isEqualTo(error(13));
  }

  @Test
  public void pulling_up_list_with_two_errors_returns_errors() {
    assertThat(pullUp(list(error(13), error(14))))
        .isEqualTo(errors(list(13, 14)));
  }

  @Test
  public void value_invokeConsumer_modifying_consumer() {
    StringBuilder builder = new StringBuilder("one");
    Maybe<StringBuilder> maybe = value(builder).invokeConsumer(m -> m.append("!"));
    assertThat(maybe.value().toString())
        .isEqualTo("one!");
  }

  @Test
  public void error_invokeConsumer() {
    Maybe<Object> maybe = error(13).invokeConsumer(v -> {
      throw new RuntimeException();
    });
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void value_invoke_supplier() {
    StringBuilder builder = new StringBuilder("one");
    Maybe<StringBuilder> maybeBuilder = value(builder).invoke(() -> list());
    assertThat(maybeBuilder.value().toString())
        .isEqualTo("one");
  }

  @Test
  public void value_invoke_error_returning_supplier() {
    Maybe<String> maybe = value("abc").invoke(() -> list(13));
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void error_invoke_supplier() {
    Maybe<Object> maybe = error(13).invoke(() -> list());
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void error_invoke_error_returning_supplier() {
    Maybe<Object> maybe = error(13).invoke(() -> list(14));
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void value_invoke_modifying_function() {
    StringBuilder builder = new StringBuilder("one");
    Maybe<StringBuilder> maybe = value(builder).invoke(v -> {
      v.append("!");
      return list();
    });
    assertThat(maybe.value().toString())
        .isEqualTo("one!");
    assertThat(builder.toString())
        .isEqualTo("one!");
  }

  @Test
  public void value_invoke_error_returning_function() {
    StringBuilder builder = new StringBuilder("one");
    Maybe<StringBuilder> maybe = value(builder)
        .invoke(v -> {
          v.append("!");
          return list(13);
        });
    assertThat(maybe.errors())
        .isEqualTo(list(13));
    assertThat(builder.toString())
        .isEqualTo("one!");
  }

  @Test
  public void value_value_invoke_modifying_function() {
    StringBuilder builder = new StringBuilder("one");
    Maybe<StringBuilder> maybeBuilder = value(builder).invoke(value("!"), (v, p) -> {
      v.append(p);
      return list();
    });
    assertThat(maybeBuilder.value().toString())
        .isEqualTo("one!");
    assertThat(builder.toString())
        .isEqualTo("one!");
  }

  @Test
  public void value_error_invoke_modifying_function() {
    StringBuilder builder = new StringBuilder("one");
    Maybe<StringBuilder> maybeBuilder = value(builder).invoke(error(13), (v, p) -> {
      throw new RuntimeException();
    });
    assertThat(maybeBuilder.errors())
        .containsExactly(13);
  }

  @Test
  public void error_value_invoke_modifying_function() {
    Maybe<Object> maybeBuilder = error(13).invoke(value("!"), (v, p) -> {
      throw new RuntimeException();
    });
    assertThat(maybeBuilder.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void error_error_invoke_modifying_function() {
    Maybe<Object> maybeBuilder = error(13).invoke(error(14), (v, p) -> {
      throw new RuntimeException();
    });
    assertThat(maybeBuilder.errors())
        .isEqualTo(list(13, 14));
  }

  @Test
  public void error_invoke() {
    Maybe<Object> maybe = error(13).invoke(v -> {
      throw new RuntimeException();
    });
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void value_map() {
    Maybe<String> maybe = value("one").map((value) -> value(value + "!"));
    assertThat(maybe.value())
        .isEqualTo("one!");
  }

  @Test
  public void error_map() {
    Maybe<Object> maybe = error(13).map(value -> {
      throw new RuntimeException();
    });
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void value_map_function_returning_error() {
    Maybe<Object> maybe = value("one").map((value) -> error(13));
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void value_mapValue() {
    Maybe<String> maybe = value("one").mapValue((value) -> value + "!");
    assertThat(maybe.value())
        .isEqualTo("one!");
  }

  @Test
  public void error_mapValue() {
    Maybe <Object> maybe = error(13).mapValue(value -> {
      throw new RuntimeException();
    });
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void value_value_map() {
    Maybe<String> maybe = value("one").map(value("two"), (r1, r2) -> value(r1 + r2));
    assertThat(maybe.value())
        .isEqualTo("onetwo");
  }

  @Test
  public void value_value_map_function_returning_error() {
    Maybe<String> maybe = value("one").map(value("two"), (r1, r2) -> error(13));
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void value_error_map() {
    Maybe<String> maybe = value("one").map(error(13), (r1, r2) -> {throw new RuntimeException(); });
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void error_value_map() {
    Maybe<String> maybe = error(13).map(value("two"), (r1, r2) -> {throw new RuntimeException(); });
    assertThat(maybe.errors())
        .isEqualTo(list(13));
  }

  @Test
  public void error_error_map() {
    Maybe<String> maybe = error(13).map(error(14), (r1, r2) -> {throw new RuntimeException(); });
    assertThat(maybe.errors())
        .isEqualTo(list(13, 14));
  }

  @Test
  public void value_value_mapValue() {
    Maybe<String> maybe = value("one")
        .mapValue(value("two"), (r1, r2) -> r1 + r2);
    assertThat(maybe.value())
        .isEqualTo("onetwo");
  }

  @Test
  public void value_error_mapValue() {
    Maybe<String> maybe = value("one")
        .mapValue(error(13), (r1, r2) -> null);
    assertThat(maybe.errors())
        .containsExactly(13);
  }

  @Test
  public void error_value_mapValue() {
    Maybe<String> maybe = error(13)
        .mapValue(value("one"), (r1, r2) -> null);
    assertThat(maybe.errors())
        .containsExactly(13);
  }

  @Test
  public void error_error_mapValue() {
    Maybe<String> maybe = error(13)
        .mapValue(error(14), (r1, r2) -> null);
    assertThat(maybe.errors())
        .containsExactly(13, 14)
        .inOrder();
  }
}
