package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.parse.Maybe.error;
import static org.smoothbuild.parse.Maybe.errors;
import static org.smoothbuild.parse.Maybe.invoke;
import static org.smoothbuild.parse.Maybe.invokeWrap;
import static org.smoothbuild.parse.Maybe.result;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class MaybeTest {
  private Maybe<String> maybe;
  private Maybe<String> maybe1;
  private Maybe<String> maybe2;
  private Maybe<String> maybe3;
  private Object error;
  private Object error2;
  private Object error3;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void with_result_has_result() throws Exception {
    given(maybe = result("abc"));
    when(() -> maybe.hasResult());
    thenReturned(true);
  }

  @Test
  public void with_result_has_no_error() throws Exception {
    given(maybe = result("abc"));
    when(() -> maybe.errors());
    thenReturned(empty());
  }

  @Test
  public void with_result_contains_result() throws Exception {
    given(maybe = result("abc"));
    when(() -> maybe.result());
    thenReturned("abc");
  }

  @Test
  public void with_error_contains_error() throws Exception {
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
  public void with_error_fails_for_null() throws Exception {
    when(() -> error(null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void with_error_has_no_result() throws Exception {
    given(maybe = error(error));
    when(() -> maybe.hasResult());
    thenReturned(false);
  }

  @Test
  public void with_error_fails_for_result() throws Exception {
    given(maybe = error(error));
    when(() -> maybe.result());
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void with_errors_contains_error() throws Exception {
    given(maybe = Maybe.errors(asList(error, error2)));
    when(() -> maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void with_errors_fails_for_null() throws Exception {
    when(() -> Maybe.errors(null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void with_errors_has_no_result() throws Exception {
    given(maybe = Maybe.errors(asList(error, error2)));
    when(() -> maybe.hasResult());
    thenReturned(false);
  }

  @Test
  public void with_errors_fails_for_result() throws Exception {
    given(maybe = Maybe.errors(asList(error, error2)));
    when(() -> maybe.result());
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void result_with_added_error_has_no_result() throws Exception {
    given(maybe = result("result"));
    given(maybe = maybe.addError(error));
    when(() -> maybe.hasResult());
    thenReturned(false);
  }

  @Test
  public void result_with_added_errors_has_no_result() throws Exception {
    given(maybe = result("result"));
    given(maybe = maybe.addErrors(asList(error, error2)));
    when(() -> maybe.hasResult());
    thenReturned(false);
  }

  @Test
  public void result_with_added_empty_errors_has_result() throws Exception {
    given(maybe = result("result"));
    given(maybe = maybe.addErrors(asList()));
    when(() -> maybe.hasResult());
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
  public void maybe_with_equal_results_are_equal() throws Exception {
    given(maybe = result("abc"));
    given(maybe2 = result("abc"));
    thenEqual(maybe, maybe2);
  }

  @Test
  public void maybe_with_equal_errors_are_equal() throws Exception {
    given(maybe = error("abc"));
    given(maybe2 = error("abc"));
    thenEqual(maybe, maybe2);
  }

  @Test
  public void maybe_result_is_not_equal_to_maybe_error() throws Exception {
    given(maybe = result("abc"));
    given(maybe2 = error("abc"));
    when(() -> maybe.equals(maybe2));
    thenReturned(false);
  }

  @Test
  public void error_has_different_hashcode_than_result() throws Exception {
    given(maybe = result("abc"));
    given(maybe2 = error("abc"));
    then(maybe.hashCode() != maybe2.hashCode());
  }

  @Test
  public void to_string_result() throws Exception {
    given(maybe = result("abc"));
    when(() -> maybe.toString());
    thenReturned("Maybe.result(abc)");
  }

  @Test
  public void to_string_error() throws Exception {
    given(maybe = error("abc"));
    when(() -> maybe.toString());
    thenReturned("Maybe.error(abc)");
  }

  @Test
  public void function_invoke_with_result() throws Exception {
    given(maybe = result("one"));
    given(maybe = invoke(maybe, (result) -> result(result + "!")));
    when(maybe.result());
    thenReturned("one!");
  }

  @Test
  public void function_invoke_with_error() throws Exception {
    given(maybe = error(error));
    given(maybe = invoke(maybe, (result) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void function_invoke_returning_error() throws Exception {
    given(maybe = result("one"));
    given(maybe = invoke(maybe, (result) -> error(error)));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void function_invokeWrap_with_result() throws Exception {
    given(maybe = result("one"));
    given(maybe = invokeWrap(maybe, (result) -> result + "!"));
    when(maybe.result());
    thenReturned("one!");
  }

  @Test
  public void function_invokeWrap_with_error() throws Exception {
    given(maybe = error(error));
    given(maybe = invokeWrap(maybe, (result) -> result));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_two_results() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> result(r1 + r2)));
    when(maybe.result());
    thenReturned("onetwo");
  }

  @Test
  public void bifunction_invoke_with_two_results_returning_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> error(error)));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_result_and_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = error(error));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_error_and_result() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = result("one"));
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
  public void bifunction_invokeWrap_with_two_results() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe = invokeWrap(maybe1, maybe2, (r1, r2) -> r1 + r2));
    when(maybe.result());
    thenReturned("onetwo");
  }

  @Test
  public void bifunction_invokeWrap_with_result_and_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = error(error));
    given(maybe = invokeWrap(maybe1, maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invokeWrap_with_error_and_result() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = result("one"));
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
  public void trifunction_invoke_with_three_results() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe3 = result("three"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> result(r1 + r2 + r3)));
    when(maybe.result());
    thenReturned("onetwothree");
  }

  @Test
  public void trifunction_invoke_with_three_results_returning_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe3 = result("three"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> error(error)));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void trifunction_invoke_with_result_result_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error3));
  }

  @Test
  public void trifunction_invoke_with_result_error_result() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = error(error2));
    given(maybe3 = result("three"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error2));
  }

  @Test
  public void trifunction_invoke_with_result_error_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = error(error2));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error2, error3));
  }

  @Test
  public void trifunction_invoke_with_error_result_result() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = result("two"));
    given(maybe3 = result("one"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void trifunction_invoke_with_error_result_error() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = result("two"));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> {
      throw new RuntimeException();
    }));
    when(maybe.errors());
    thenReturned(asList(error, error3));
  }

  @Test
  public void trifunction_invoke_with_error_error_result() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe3 = result("two"));
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
  public void trifunction_invokeWrap_with_three_results() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe3 = result("three"));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> r1 + r2 + r3));
    when(maybe.result());
    thenReturned("onetwothree");
  }

  @Test
  public void trifunction_invokeWrap_with_result_result_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe3 = error(error3));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error3));
  }

  @Test
  public void trifunction_invokeWrap_with_result_error_result() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = error(error2));
    given(maybe3 = result("three"));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error2));
  }

  @Test
  public void trifunction_invokeWrap_with_result_error_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = error(error2));
    given(maybe3 = error(error3));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error2, error3));
  }

  @Test
  public void trifunction_invokeWrap_with_error_result_result() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = result("two"));
    given(maybe3 = result("one"));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void trifunction_invokeWrap_with_error_result_error() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = result("two"));
    given(maybe3 = error(error3));
    given(maybe = invokeWrap(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error, error3));
  }

  @Test
  public void trifunction_invokeWrap_with_error_error_result() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe3 = result("two"));
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
