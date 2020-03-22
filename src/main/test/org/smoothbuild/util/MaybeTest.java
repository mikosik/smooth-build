package org.smoothbuild.util;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Maybe.error;
import static org.smoothbuild.util.Maybe.errors;
import static org.smoothbuild.util.Maybe.pullUp;
import static org.smoothbuild.util.Maybe.value;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MaybeTest {
  private Maybe<String> maybe;
  private Maybe<String> maybe1;
  private Maybe<String> maybe2;
  private Maybe<StringBuilder> maybeBuilder;
  private List<Maybe<String>> listOfMaybe;
  private final Object error = new Object();
  private final Object error2 = new Object();
  private final Object error3 = new Object();
  private StringBuilder builder;

  @BeforeEach
  public void before() {}

  @Test
  public void created_value_has_value() throws Exception {
    given(maybe = value("abc"));
    when(() -> maybe.hasValue());
    thenReturned(true);
  }

  @Test
  public void created_value_has_no_error() throws Exception {
    given(maybe = value("abc"));
    when(() -> maybe.errors());
    thenReturned(empty());
  }

  @Test
  public void created_value_contains_value() throws Exception {
    given(maybe = value("abc"));
    when(() -> maybe.value());
    thenReturned("abc");
  }

  @Test
  public void created_error_contains_error() throws Exception {
    given(maybe = error(error));
    when(() -> maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void errors_static_factory_accepts_list_of_strings() throws Exception {
    given(maybe = errors(list("abc", "def")));
    when(() -> maybe.errors());
    thenReturned(list("abc", "def"));
  }

  @Test
  public void created_error_fails_for_null() throws Exception {
    when(() -> error(null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void created_error_has_no_value() throws Exception {
    given(maybe = error(error));
    when(() -> maybe.hasValue());
    thenReturned(false);
  }

  @Test
  public void created_error_fails_for_value() throws Exception {
    given(maybe = error(error));
    when(() -> maybe.value());
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void created_errors_contains_error() throws Exception {
    given(maybe = Maybe.errors(list(error, error2)));
    when(() -> maybe.errors());
    thenReturned(list(error, error2));
  }

  @Test
  public void created_errors_fails_for_null() throws Exception {
    when(() -> Maybe.errors(null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void created_errors_fails_for_empty_list() throws Exception {
    when(() -> Maybe.errors(list()));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void created_errors_has_no_value() throws Exception {
    given(maybe = Maybe.errors(list(error, error2)));
    when(() -> maybe.hasValue());
    thenReturned(false);
  }

  @Test
  public void created_errors_fails_for_value() throws Exception {
    given(maybe = Maybe.errors(list(error, error2)));
    when(() -> maybe.value());
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void created_maybe_with_null_value_and_no_errors_has_null_value() throws Exception {
    given(maybe = Maybe.maybe(null, list()));
    when(() -> maybe.value());
    thenReturned(null);
  }

  @Test
  public void created_maybe_with_value_and_no_errors_has_value() throws Exception {
    given(maybe = Maybe.maybe("value", list()));
    when(() -> maybe.value());
    thenReturned("value");
  }

  @Test
  public void created_maybe_with_value_and_no_errors_has_no_errors() throws Exception {
    given(maybe = Maybe.maybe("value", list()));
    when(() -> maybe.errors());
    thenReturned(hasSize(0));
  }

  @Test
  public void created_maybe_with_null_value_and_errors_fails_for_value() throws Exception {
    given(maybe = Maybe.maybe(null, list(error, error2)));
    when(() -> maybe.value());
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void created_maybe_with_null_value_and_errors_has_errors() throws Exception {
    given(maybe = Maybe.maybe(null, list(error, error2)));
    when(() -> maybe.errors());
    thenReturned(list(error, error2));
  }

  @Test
  public void created_maybe_with_value_and_errors_fails_for_value() throws Exception {
    given(maybe = Maybe.maybe("value", list(error, error2)));
    when(() -> maybe.value());
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void created_maybe_with_value_and_errors_has_errors() throws Exception {
    given(maybe = Maybe.maybe("value", list(error, error2)));
    when(() -> maybe.errors());
    thenReturned(list(error, error2));
  }

  @Test
  public void value_addError_has_no_value() throws Exception {
    given(maybe = value("value"));
    given(maybe = maybe.addError(error));
    when(() -> maybe.hasValue());
    thenReturned(false);
  }

  @Test
  public void value_addErrors_has_no_value() throws Exception {
    given(maybe = value("value"));
    given(maybe = maybe.addErrors(list(error, error2)));
    when(() -> maybe.hasValue());
    thenReturned(false);
  }

  @Test
  public void value_addErrors_empty_list_has_value() throws Exception {
    given(maybe = value("value"));
    given(maybe = maybe.addErrors(list()));
    when(() -> maybe.hasValue());
    thenReturned(true);
  }

  @Test
  public void error_addError_has_both() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.addError(error2));
    when(() -> maybe.errors());
    thenReturned(list(error, error2));
  }

  @Test
  public void error_addErrors_has_all_errors() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.addErrors(list(error2, error3)));
    when(() -> maybe.errors());
    thenReturned(list(error, error2, error3));
  }

  @Test
  public void error_addErrors_empty_list_has_initial_error() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.addErrors(list()));
    when(() -> maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void maybes_with_equal_values_are_equal() throws Exception {
    given(maybe = value("abc"));
    given(maybe2 = value("abc"));
    thenEqual(maybe, maybe2);
  }

  @Test
  public void maybes_with_equal_errors_are_equal() throws Exception {
    given(maybe = error("abc"));
    given(maybe2 = error("abc"));
    thenEqual(maybe, maybe2);
  }

  @Test
  public void maybe_value_is_not_equal_to_maybe_error() throws Exception {
    given(maybe = value("abc"));
    given(maybe2 = error("abc"));
    when(() -> maybe.equals(maybe2));
    thenReturned(false);
  }

  @Test
  public void error_has_different_hashcode_than_value() throws Exception {
    given(maybe = value("abc"));
    given(maybe2 = error("abc"));
    then(maybe.hashCode() != maybe2.hashCode());
  }

  @Test
  public void to_string_value() throws Exception {
    given(maybe = value("abc"));
    when(() -> maybe.toString());
    thenReturned("Maybe.value(abc)");
  }

  @Test
  public void to_string_error() throws Exception {
    given(maybe = error("abc"));
    when(() -> maybe.toString());
    thenReturned("Maybe.error(abc)");
  }

  @Test
  public void pulling_up_empty_list_gives_value_with_empty_list() throws Exception {
    given(listOfMaybe = list());
    when(() -> pullUp(listOfMaybe));
    thenReturned(value(list()));
  }

  @Test
  public void pulling_up_list_with_value_returns_value_with_one_element() throws Exception {
    given(listOfMaybe = list(value("abc")));
    when(() -> pullUp(listOfMaybe));
    thenReturned(value(list("abc")));
  }

  @Test
  public void pulling_up_list_with_error_returns_error() throws Exception {
    given(listOfMaybe = list(error(error)));
    when(() -> pullUp(listOfMaybe));
    thenReturned(error(error));
  }

  @Test
  public void pulling_up_list_with_value_and_error_returns_error() throws Exception {
    given(listOfMaybe = list(value("abc"), error(error)));
    when(() -> pullUp(listOfMaybe));
    thenReturned(error(error));
  }

  @Test
  public void pulling_up_list_with_two_errors_returns_errors() throws Exception {
    given(listOfMaybe = list(error(error), error(error2)));
    when(() -> pullUp(listOfMaybe));
    thenReturned(errors(list(error, error2)));
  }

  @Test
  public void value_invokeConsumer_modifying_consumer() throws Exception {
    given(builder = new StringBuilder("one"));
    given(maybeBuilder = value(builder));
    given(maybeBuilder = maybeBuilder.invokeConsumer(v -> v.append("!")));
    when(() -> maybeBuilder.value().toString());
    thenReturned("one!");
    thenEqual(builder.toString(), "one!");
  }

  @Test
  public void error_invokeConsumer() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.invokeConsumer(v -> {
      throw new RuntimeException();
    }));
    when(() -> maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void value_invoke_supplier() throws Exception {
    given(builder = new StringBuilder("one"));
    given(maybeBuilder = value(builder));
    given(maybeBuilder = maybeBuilder.invoke(() -> list()));
    when(() -> maybeBuilder.value().toString());
    thenReturned("one");
  }

  @Test
  public void value_invoke_error_returning_supplier() throws Exception {
    given(builder = new StringBuilder("one"));
    given(maybeBuilder = value(builder));
    given(maybeBuilder = maybeBuilder.invoke(() -> list(error)));
    when(() -> maybeBuilder.errors());
    thenReturned(list(error));
  }

  @Test
  public void error_invoke_supplier() throws Exception {
    given(builder = new StringBuilder("one"));
    given(maybeBuilder = error(error));
    given(maybeBuilder = maybeBuilder.invoke(() -> list()));
    when(() -> maybeBuilder.errors());
    thenReturned(list(error));
  }

  @Test
  public void error_invoke_error_returning_supplier() throws Exception {
    given(builder = new StringBuilder("one"));
    given(maybeBuilder = error(error));
    given(maybeBuilder = maybeBuilder.invoke(() -> list(error2)));
    when(() -> maybeBuilder.errors());
    thenReturned(list(error));
  }

  @Test
  public void value_invoke_modifying_function() throws Exception {
    given(builder = new StringBuilder("one"));
    given(maybeBuilder = value(builder));
    given(maybeBuilder = maybeBuilder.invoke(v -> {
      v.append("!");
      return list();
    }));
    when(() -> maybeBuilder.value().toString());
    thenReturned("one!");
    thenEqual(builder.toString(), "one!");
  }

  @Test
  public void value_invoke_error_returning_function() throws Exception {
    given(builder = new StringBuilder("one"));
    given(maybeBuilder = value(builder));
    given(maybeBuilder = maybeBuilder.invoke(v -> {
      v.append("!");
      return list(error);
    }));
    when(() -> maybeBuilder.errors());
    thenReturned(list(error));
    thenEqual(builder.toString(), "one!");
  }

  @Test
  public void value_value_invoke_modifying_function() throws Exception {
    given(builder = new StringBuilder("one"));
    given(maybeBuilder = value(builder));
    given(maybe = value("!"));
    given(maybeBuilder = maybeBuilder.invoke(maybe, (v, p) -> {
      v.append(p);
      return list();
    }));
    when(() -> maybeBuilder.value().toString());
    thenReturned("one!");
    thenEqual(builder.toString(), "one!");
  }

  @Test
  public void value_error_invoke_modifying_function() throws Exception {
    given(builder = new StringBuilder("one"));
    given(maybeBuilder = value(builder));
    given(maybe = error(error));
    given(maybeBuilder = maybeBuilder.invoke(maybe, (v, p) -> {
      throw new RuntimeException();
    }));
    when(() -> maybeBuilder.errors());
    thenReturned(list(error));
  }

  @Test
  public void error_value_invoke_modifying_function() throws Exception {
    given(maybeBuilder = error(error));
    given(maybe = value("!"));
    given(maybeBuilder = maybeBuilder.invoke(maybe, (v, p) -> {
      throw new RuntimeException();
    }));
    when(() -> maybeBuilder.errors());
    thenReturned(list(error));
  }

  @Test
  public void error_error_invoke_modifying_function() throws Exception {
    given(maybeBuilder = error(error));
    given(maybe = error(error2));
    given(maybeBuilder = maybeBuilder.invoke(maybe, (v, p) -> {
      throw new RuntimeException();
    }));
    when(() -> maybeBuilder.errors());
    thenReturned(list(error, error2));
  }

  @Test
  public void error_invoke() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.invoke(v -> {
      throw new RuntimeException();
    }));
    when(() -> maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void value_map() throws Exception {
    given(maybe = value("one"));
    given(maybe = maybe.map((value) -> value(value + "!")));
    when(maybe.value());
    thenReturned("one!");
  }

  @Test
  public void error_map() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.map(value -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void value_map_function_returning_error() throws Exception {
    given(maybe = value("one"));
    given(maybe = maybe.map((value) -> error(error)));
    when(maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void value_mapValue() throws Exception {
    given(maybe = value("one"));
    given(maybe = maybe.mapValue((value) -> value + "!"));
    when(maybe.value());
    thenReturned("one!");
  }

  @Test
  public void error_mapValue() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.mapValue(value -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void value_value_map() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe = maybe1.map(maybe2, (r1, r2) -> value(r1 + r2)));
    when(maybe.value());
    thenReturned("onetwo");
  }

  @Test
  public void value_value_map_function_returning_error() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe = maybe1.map(maybe2, (r1, r2) -> error(error)));
    when(maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void value_error_map() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = error(error));
    given(maybe = maybe1.map(maybe2, (r1, r2) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void error_value_map() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = value("one"));
    given(maybe = maybe1.map(maybe2, (r1, r2) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void error_error_map() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe = maybe1.map(maybe2, (r1, r2) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(list(error, error2));
  }

  @Test
  public void value_value_mapValue() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe = maybe1.mapValue(maybe2, (r1, r2) -> r1 + r2));
    when(maybe.value());
    thenReturned("onetwo");
  }

  @Test
  public void value_error_mapValue() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = error(error));
    given(maybe = maybe1.mapValue(maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void error_value_mapValue() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = value("one"));
    given(maybe = maybe1.mapValue(maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(list(error));
  }

  @Test
  public void error_error_mapValue() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe = maybe1.mapValue(maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(list(error, error2));
  }
}
