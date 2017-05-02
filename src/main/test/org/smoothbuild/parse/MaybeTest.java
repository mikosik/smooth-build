package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.smoothbuild.parse.Maybe.error;
import static org.smoothbuild.parse.Maybe.errors;
import static org.smoothbuild.parse.Maybe.invoke;
import static org.smoothbuild.parse.Maybe.invokeWrap;
import static org.smoothbuild.parse.Maybe.pullUp;
import static org.smoothbuild.parse.Maybe.value;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class MaybeTest {
  private Maybe<String> maybe;
  private Maybe<String> maybe1;
  private Maybe<String> maybe2;
  private Maybe<String> maybe3;
  private List<Maybe<String>> listOfMaybe;
  private Object error;
  private Object error2;
  private Object error3;

  @Before
  public void before() {
    givenTest(this);
  }

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
    thenReturned(asList(error));
  }

  @Test
  public void errors_static_factory_accepts_list_of_strings() throws Exception {
    given(maybe = errors(Arrays.<String> asList("abc", "def")));
    when(() -> maybe.errors());
    thenReturned(Arrays.asList("abc", "def"));
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
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void created_errors_contains_error() throws Exception {
    given(maybe = Maybe.errors(asList(error, error2)));
    when(() -> maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void created_errors_fails_for_null() throws Exception {
    when(() -> Maybe.errors(null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void created_errors_fails_for_empty_list() throws Exception {
    when(() -> Maybe.errors(asList()));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void created_errors_has_no_value() throws Exception {
    given(maybe = Maybe.errors(asList(error, error2)));
    when(() -> maybe.hasValue());
    thenReturned(false);
  }

  @Test
  public void created_errors_fails_for_value() throws Exception {
    given(maybe = Maybe.errors(asList(error, error2)));
    when(() -> maybe.value());
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void created_maybe_with_null_value_and_no_errors_fails() throws Exception {
    when(() -> Maybe.maybe(null, asList()));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void created_maybe_with_value_and_no_errors_has_value() throws Exception {
    given(maybe = Maybe.maybe("value", asList()));
    when(() -> maybe.value());
    thenReturned("value");
  }

  @Test
  public void created_maybe_with_value_and_no_errors_has_no_errors() throws Exception {
    given(maybe = Maybe.maybe("value", asList()));
    when(() -> maybe.errors());
    thenReturned(hasSize(0));
  }

  @Test
  public void created_maybe_with_null_value_and_errors_fails_for_value() throws Exception {
    given(maybe = Maybe.maybe(null, asList(error, error2)));
    when(() -> maybe.value());
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void created_maybe_with_null_value_and_errors_has_errors() throws Exception {
    given(maybe = Maybe.maybe(null, asList(error, error2)));
    when(() -> maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void created_maybe_with_value_and_errors_fails_for_value() throws Exception {
    given(maybe = Maybe.maybe("value", asList(error, error2)));
    when(() -> maybe.value());
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void created_maybe_with_value_and_errors_has_errors() throws Exception {
    given(maybe = Maybe.maybe("value", asList(error, error2)));
    when(() -> maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void value_with_added_error_has_no_value() throws Exception {
    given(maybe = value("value"));
    given(maybe = maybe.addError(error));
    when(() -> maybe.hasValue());
    thenReturned(false);
  }

  @Test
  public void value_with_added_errors_has_no_value() throws Exception {
    given(maybe = value("value"));
    given(maybe = maybe.addErrors(asList(error, error2)));
    when(() -> maybe.hasValue());
    thenReturned(false);
  }

  @Test
  public void value_with_added_empty_errors_has_value() throws Exception {
    given(maybe = value("value"));
    given(maybe = maybe.addErrors(asList()));
    when(() -> maybe.hasValue());
    thenReturned(true);
  }

  @Test
  public void error_with_added_error_has_both() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.addError(error2));
    when(() -> maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void error_with_added_errors_has_all_errors() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.addErrors(asList(error2, error3)));
    when(() -> maybe.errors());
    thenReturned(asList(error, error2, error3));
  }

  @Test
  public void error_with_added_empty_errors_initial_error() throws Exception {
    given(maybe = error(error));
    given(maybe = maybe.addErrors(asList()));
    when(() -> maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void maybe_with_equal_values_are_equal() throws Exception {
    given(maybe = value("abc"));
    given(maybe2 = value("abc"));
    thenEqual(maybe, maybe2);
  }

  @Test
  public void maybe_with_equal_errors_are_equal() throws Exception {
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
    given(listOfMaybe = asList());
    when(() -> pullUp(listOfMaybe));
    thenReturned(value(asList()));
  }

  @Test
  public void pulling_up_list_with_value_returns_value_with_one_element() throws Exception {
    given(listOfMaybe = asList(value("abc")));
    when(() -> pullUp(listOfMaybe));
    thenReturned(value(asList("abc")));
  }

  @Test
  public void pulling_up_list_with_error_returns_error() throws Exception {
    given(listOfMaybe = asList(error(error)));
    when(() -> pullUp(listOfMaybe));
    thenReturned(error(error));
  }

  @Test
  public void pulling_up_list_with_value_and_error_returns_error() throws Exception {
    given(listOfMaybe = asList(value("abc"), error(error)));
    when(() -> pullUp(listOfMaybe));
    thenReturned(error(error));
  }

  @Test
  public void pulling_up_list_with_two_errors_returns_errors() throws Exception {
    given(listOfMaybe = asList(error(error), error(error2)));
    when(() -> pullUp(listOfMaybe));
    thenReturned(errors(asList(error, error2)));
  }

  @Test
  public void function_invoke_with_value() throws Exception {
    given(maybe = value("one"));
    given(maybe = invoke(maybe, (value) -> value(value + "!")));
    when(maybe.value());
    thenReturned("one!");
  }

  @Test
  public void function_invoke_with_error() throws Exception {
    given(maybe = error(error));
    given(maybe = invoke(maybe, (value) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void function_invoke_returning_error() throws Exception {
    given(maybe = value("one"));
    given(maybe = invoke(maybe, (value) -> error(error)));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void function_invokeWrap_with_value() throws Exception {
    given(maybe = value("one"));
    given(maybe = invokeWrap(maybe, (value) -> value + "!"));
    when(maybe.value());
    thenReturned("one!");
  }

  @Test
  public void function_invokeWrap_with_error() throws Exception {
    given(maybe = error(error));
    given(maybe = invokeWrap(maybe, (value) -> value));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_two_values() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> value(r1 + r2)));
    when(maybe.value());
    thenReturned("onetwo");
  }

  @Test
  public void bifunction_invoke_with_two_values_returning_error() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> error(error)));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_value_and_error() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = error(error));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_error_and_value() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = value("one"));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_two_errors() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void bifunction_invokeWrap_with_two_values() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe = invokeWrap(maybe1, maybe2, (r1, r2) -> r1 + r2));
    when(maybe.value());
    thenReturned("onetwo");
  }

  @Test
  public void bifunction_invokeWrap_with_value_and_error() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = error(error));
    given(maybe = invokeWrap(maybe1, maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invokeWrap_with_error_and_value() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = value("one"));
    given(maybe = invokeWrap(maybe1, maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invokeWrap_with_two_errors() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe = invokeWrap(maybe1, maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void trifunction_invoke_with_three_values() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe3 = value("three"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> value(r1 + r2 + r3)));
    when(maybe.value());
    thenReturned("onetwothree");
  }

  @Test
  public void trifunction_invoke_with_three_values_returning_error() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe3 = value("three"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> error(error)));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void trifunction_invoke_with_value_value_error() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error3));
  }

  @Test
  public void trifunction_invoke_with_value_error_value() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = error(error2));
    given(maybe3 = value("three"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error2));
  }

  @Test
  public void trifunction_invoke_with_value_error_error() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = error(error2));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error2, error3));
  }

  @Test
  public void trifunction_invoke_with_error_value_value() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = value("two"));
    given(maybe3 = value("one"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void trifunction_invoke_with_error_value_error() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = value("two"));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error, error3));
  }

  @Test
  public void trifunction_invoke_with_error_error_value() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe3 = value("two"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void trifunction_invoke_with_error_error_error() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error, error2, error3));
  }

  @Test
  public void trifunction_invokeWrap_with_three_values() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe3 = value("three"));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> r1 + r2 + r3));
    when(maybe.value());
    thenReturned("onetwothree");
  }

  @Test
  public void trifunction_invokeWrap_with_value_value_error() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = value("two"));
    given(maybe3 = error(error3));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error3));
  }

  @Test
  public void trifunction_invokeWrap_with_value_error_value() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = error(error2));
    given(maybe3 = value("three"));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error2));
  }

  @Test
  public void trifunction_invokeWrap_with_value_error_error() throws Exception {
    given(maybe1 = value("one"));
    given(maybe2 = error(error2));
    given(maybe3 = error(error3));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error2, error3));
  }

  @Test
  public void trifunction_invokeWrap_with_error_value_value() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = value("two"));
    given(maybe3 = value("one"));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void trifunction_invokeWrap_with_error_value_error() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = value("two"));
    given(maybe3 = error(error3));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error, error3));
  }

  @Test
  public void trifunction_invokeWrap_with_error_error_value() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe3 = value("two"));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void trifunction_invokeWrap_with_error_error_error() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe3 = error(error3));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error, error2, error3));
  }
}
