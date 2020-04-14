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
    Maybe<String> maybe = error("err");
    assertThat(maybe.errors())
        .containsExactly("err");
  }

  @Test
  public void errors_static_factory_accepts_list_of_errors() {
    Maybe<String> maybe = errors(list("err1", "err2"));
    assertThat(maybe.errors())
        .containsExactly("err1", "err2")
        .inOrder();
  }

  @Test
  public void created_error_fails_for_null() {
    assertCall(() -> error(null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void created_error_has_no_value() {
    Maybe<String> maybe = error("err");
    assertThat(maybe.hasValue())
        .isFalse();
  }

  @Test
  public void created_error_fails_for_value() {
    Maybe<String> maybe = error("err");
    assertCall(maybe::value)
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void created_errors_contains_error() {
    Maybe<String> maybe = errors(list("err1", "err2"));
    assertThat(maybe.errors())
        .containsExactly("err1", "err2")
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
    Maybe<String> maybe = errors(list("err1", "err2"));
    assertThat(maybe.hasValue())
        .isFalse();
  }

  @Test
  public void created_errors_fails_for_value() {
    Maybe<String> maybe = errors(list("err1", "err2"));
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
    Maybe<String> maybe = Maybe.maybe(null, list("err1", "err2"));
    assertCall(maybe::value)
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void created_maybe_with_null_value_and_errors_has_errors() {
    Maybe<String> maybe = Maybe.maybe(null, list("err1", "err2"));
    assertThat(maybe.errors())
        .containsExactly("err1", "err2")
        .inOrder();
  }

  @Test
  public void created_maybe_with_value_and_errors_fails_for_value() {
    Maybe<String> maybe = Maybe.maybe("value", list("err1", "err2"));
    assertCall(maybe::value)
        .throwsException(UnsupportedOperationException.class);
  }

  @Test
  public void created_maybe_with_value_and_errors_has_errors() {
    Maybe<String> maybe = Maybe.maybe("value", list("err1", "err2"));
    assertThat(maybe.errors())
        .containsExactly("err1", "err2")
        .inOrder();
  }

  @Test
  public void value_addError_has_no_value() {
    Maybe<String> maybe = value("value").addError("err");
    assertThat(maybe.hasValue())
        .isFalse();
  }

  @Test
  public void value_addErrors_has_no_value() {
    Maybe<String> maybe = value("value").addErrors(list("err1", "err2"));
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
    Maybe<Object> maybe = error("err1").addError("err2");
    assertThat(maybe.errors())
        .containsExactly("err1", "err2")
        .inOrder();
  }

  @Test
  public void error_addErrors_has_all_errors() {
    Maybe<Object> maybe = error("err1").addErrors(list("err2", "err3"));
    assertThat(maybe.errors())
        .containsExactly("err1", "err2", "err3")
        .inOrder();
  }

  @Test
  public void error_addErrors_empty_list_has_initial_error() {
    Maybe<Object> maybe = error("err").addErrors(list());
    assertThat(maybe.errors())
        .containsExactly("err");
  }

  @Test
  public void maybes_with_equal_values_are_equal() {
    assertThat(value("abc"))
        .isEqualTo(value("abc"));
  }

  @Test
  public void maybes_with_equal_errors_are_equal() {
    assertThat(error("err"))
        .isEqualTo(error("err"));
  }

  @Test
  public void maybe_value_is_not_equal_to_maybe_error() {
    assertThat(value("abc"))
        .isNotEqualTo(error("err"));
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
    assertThat(pullUp(list(error("err"))))
        .isEqualTo(error("err"));
  }

  @Test
  public void pulling_up_list_with_value_and_error_returns_error() {
    assertThat(pullUp(list(value("abc"), error("err"))))
        .isEqualTo(error("err"));
  }

  @Test
  public void pulling_up_list_with_two_errors_returns_errors() {
    assertThat(pullUp(list(error("err1"), error("err2"))))
        .isEqualTo(errors(list("err1", "err2")));
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
    Maybe<Object> maybe = error("err").invokeConsumer(v -> {
      throw new RuntimeException();
    });
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
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
    Maybe<String> maybe = value("abc").invoke(() -> list("err"));
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void error_invoke_supplier() {
    Maybe<Object> maybe = error("err").invoke(() -> list());
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void error_invoke_error_returning_supplier() {
    Maybe<Object> maybe = error("err1").invoke(() -> list("err2"));
    assertThat(maybe.errors())
        .isEqualTo(list("err1"));
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
          return list("err");
        });
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
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
    Maybe<StringBuilder> maybeBuilder = value(builder).invoke(error("err"), (v, p) -> {
      throw new RuntimeException();
    });
    assertThat(maybeBuilder.errors())
        .containsExactly("err");
  }

  @Test
  public void error_value_invoke_modifying_function() {
    Maybe<Object> maybeBuilder = error("err").invoke(value("!"), (v, p) -> {
      throw new RuntimeException();
    });
    assertThat(maybeBuilder.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void error_error_invoke_modifying_function() {
    Maybe<Object> maybeBuilder = error("err1").invoke(error("err2"), (v, p) -> {
      throw new RuntimeException();
    });
    assertThat(maybeBuilder.errors())
        .isEqualTo(list("err1", "err2"));
  }

  @Test
  public void error_invoke() {
    Maybe<Object> maybe = error("err").invoke(v -> {
      throw new RuntimeException();
    });
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void value_map() {
    Maybe<String> maybe = value("one").map((value) -> value(value + "!"));
    assertThat(maybe.value())
        .isEqualTo("one!");
  }

  @Test
  public void error_map() {
    Maybe<Object> maybe = error("err").map(value -> {
      throw new RuntimeException();
    });
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void value_map_function_returning_error() {
    Maybe<Object> maybe = value("one").map((value) -> error("err"));
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void value_mapValue() {
    Maybe<String> maybe = value("one").mapValue((value) -> value + "!");
    assertThat(maybe.value())
        .isEqualTo("one!");
  }

  @Test
  public void error_mapValue() {
    Maybe <Object> maybe = error("err").mapValue(value -> {
      throw new RuntimeException();
    });
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void value_value_map() {
    Maybe<String> maybe = value("one").map(value("two"), (r1, r2) -> value(r1 + r2));
    assertThat(maybe.value())
        .isEqualTo("onetwo");
  }

  @Test
  public void value_value_map_function_returning_error() {
    Maybe<String> maybe = value("one").map(value("two"), (r1, r2) -> error("err"));
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void value_error_map() {
    Maybe<String> maybe = value("one").map(error("err"), (r1, r2) -> {throw new RuntimeException(); });
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void error_value_map() {
    Maybe<String> maybe = error("err").map(value("two"), (r1, r2) -> {throw new RuntimeException(); });
    assertThat(maybe.errors())
        .isEqualTo(list("err"));
  }

  @Test
  public void error_error_map() {
    Maybe<String> maybe = error("err1")
        .map(error("err2"), (r1, r2) -> {throw new RuntimeException(); });
    assertThat(maybe.errors())
        .isEqualTo(list("err1", "err2"));
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
        .mapValue(error("err"), (r1, r2) -> null);
    assertThat(maybe.errors())
        .containsExactly("err");
  }

  @Test
  public void error_value_mapValue() {
    Maybe<String> maybe = error("err")
        .mapValue(value("one"), (r1, r2) -> null);
    assertThat(maybe.errors())
        .containsExactly("err");
  }

  @Test
  public void error_error_mapValue() {
    Maybe<String> maybe = error("err1")
        .mapValue(error("err2"), (r1, r2) -> null);
    assertThat(maybe.errors())
        .containsExactly("err1", "err2")
        .inOrder();
  }
}
