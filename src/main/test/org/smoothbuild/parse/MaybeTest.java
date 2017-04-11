package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.parse.Maybe.result;
import static org.smoothbuild.parse.Maybe.error;
import static org.smoothbuild.parse.Maybe.invoke;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

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
    given(maybe = Maybe.result("abc"));
    when(() -> maybe.hasResult());
    thenReturned(true);
  }

  @Test
  public void with_result_has_no_error() throws Exception {
    given(maybe = Maybe.result("abc"));
    when(() -> maybe.errors());
    thenReturned(empty());
  }

  @Test
  public void with_result_contains_result() throws Exception {
    given(maybe = Maybe.result("abc"));
    when(() -> maybe.result());
    thenReturned("abc");
  }

  @Test
  public void with_error_contains_error() throws Exception {
    given(maybe = Maybe.error(error));
    when(() -> maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void with_error_fails_for_null() throws Exception {
    when(() -> Maybe.error(null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void with_error_has_no_result() throws Exception {
    given(maybe = Maybe.error(error));
    when(() -> maybe.hasResult());
    thenReturned(false);
  }

  @Test
  public void with_error_fails_for_result() throws Exception {
    given(maybe = Maybe.error(error));
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
    given(maybe = Maybe.result("result"));
    given(maybe = maybe.addError(error));
    when(() -> maybe.hasResult());
    thenReturned(false);
  }

  @Test
  public void result_with_added_errors_has_no_result() throws Exception {
    given(maybe = Maybe.result("result"));
    given(maybe = maybe.addErrors(asList(error, error2)));
    when(() -> maybe.hasResult());
    thenReturned(false);
  }

  @Test
  public void error_with_added_error_has_both() throws Exception {
    given(maybe = Maybe.error(error));
    given(maybe = maybe.addError(error2));
    when(() -> maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void error_with_added_errors_has_all_errors() throws Exception {
    given(maybe = Maybe.error(error));
    given(maybe = maybe.addErrors(asList(error2, error3)));
    when(() -> maybe.errors());
    thenReturned(asList(error, error2, error3));
  }

  @Test
  public void function_invoke_with_result() throws Exception {
    given(maybe = Maybe.result("one"));
    given(maybe = invoke(maybe, (result) -> result + "!"));
    when(maybe.result());
    thenReturned("one!");
  }

  @Test
  public void function_invoke_with_error() throws Exception {
    given(maybe = Maybe.error(error));
    given(maybe = invoke(maybe, (result) -> result));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_two_results() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> r1 + r2));
    when(maybe.result());
    thenReturned("onetwo");
  }

  @Test
  public void bifunction_invoke_with_result_and_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = error(error));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_error_and_result() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = result("one"));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void bifunction_invoke_with_two_errors() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe = invoke(maybe1, maybe2, (r1, r2) -> null));
    when(maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void trifunction_invoke_with_three_results() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe3 = result("three"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> r1 + r2 + r3));
    when(maybe.result());
    thenReturned("onetwothree");
  }

  @Test
  public void trifunction_invoke_with_result_result_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = result("two"));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error3));
  }

  @Test
  public void trifunction_invoke_with_result_error_result() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = error(error2));
    given(maybe3 = result("three"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error2));
  }

  @Test
  public void trifunction_invoke_with_result_error_error() throws Exception {
    given(maybe1 = result("one"));
    given(maybe2 = error(error2));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error2, error3));
  }

  @Test
  public void trifunction_invoke_with_error_result_result() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = result("two"));
    given(maybe3 = result("one"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error));
  }

  @Test
  public void trifunction_invoke_with_error_result_error() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = result("two"));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error, error3));
  }

  @Test
  public void trifunction_invoke_with_error_error_result() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe3 = result("two"));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error, error2));
  }

  @Test
  public void trifunction_invoke_with_error_error_error() throws Exception {
    given(maybe1 = error(error));
    given(maybe2 = error(error2));
    given(maybe3 = error(error3));
    given(maybe = invoke(maybe1, maybe2, maybe3, (r1, r2, r3) -> null));
    when(maybe.errors());
    thenReturned(asList(error, error2, error3));
  }
}
