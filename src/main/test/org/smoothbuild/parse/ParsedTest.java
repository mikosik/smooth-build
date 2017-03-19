package org.smoothbuild.parse;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.lang.message.CodeLocation.codeLocation;
import static org.smoothbuild.parse.Parsed.error;
import static org.smoothbuild.parse.Parsed.invoke;
import static org.smoothbuild.parse.Parsed.parsed;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;

public class ParsedTest {
  private Parsed<String> parsed;
  private Parsed<String> parsed1;
  private Parsed<String> parsed2;
  private Parsed<String> parsed3;

  @Test
  public void with_result_has_result() throws Exception {
    given(parsed = Parsed.parsed("abc"));
    when(() -> parsed.hasResult());
    thenReturned(true);
  }

  @Test
  public void with_result_has_no_error() throws Exception {
    given(parsed = Parsed.parsed("abc"));
    when(() -> parsed.errors());
    thenReturned(empty());
  }

  @Test
  public void with_result_contains_result() throws Exception {
    given(parsed = Parsed.parsed("abc"));
    when(() -> parsed.result());
    thenReturned("abc");
  }

  @Test
  public void with_error_contains_error() throws Exception {
    given(parsed = Parsed.error("error"));
    when(() -> parsed.errors());
    thenReturned(asList("error"));
  }

  @Test
  public void with_error_and_code_location_contains_both() throws Exception {
    given(parsed = Parsed.error(codeLocation(33), "message"));
    when(() -> parsed.errors());
    thenReturned(asList("build.smooth:33: error: message"));
  }

  @Test
  public void with_error_fails_for_null() throws Exception {
    when(() -> Parsed.error(null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void with_error_has_no_result() throws Exception {
    given(parsed = Parsed.error("error"));
    when(() -> parsed.hasResult());
    thenReturned(false);
  }

  @Test
  public void with_error_fails_for_result() throws Exception {
    given(parsed = Parsed.error("error"));
    when(() -> parsed.result());
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void result_with_added_error_has_no_result() throws Exception {
    given(parsed = Parsed.parsed("result"));
    given(parsed = parsed.addError("error"));
    when(() -> parsed.hasResult());
    thenReturned(false);
  }

  @Test
  public void result_with_added_errors_has_no_result() throws Exception {
    given(parsed = Parsed.parsed("result"));
    given(parsed = parsed.addErrors(asList("error", "error2")));
    when(() -> parsed.hasResult());
    thenReturned(false);
  }

  @Test
  public void error_with_added_error_has_both() throws Exception {
    given(parsed = Parsed.error("error1"));
    given(parsed = parsed.addError("error2"));
    when(() -> parsed.errors());
    thenReturned(asList("error1", "error2"));
  }

  @Test
  public void error_with_added_errors_has_all_errors() throws Exception {
    given(parsed = Parsed.error("error1"));
    given(parsed = parsed.addErrors(asList("error2", "error3")));
    when(() -> parsed.errors());
    thenReturned(asList("error1", "error2", "error3"));
  }

  @Test
  public void function_invoke_with_result() throws Exception {
    given(parsed = Parsed.parsed("one"));
    given(parsed = invoke(parsed, (result) -> result + "!"));
    when(parsed.result());
    thenReturned("one!");
  }

  @Test
  public void function_invoke_with_error() throws Exception {
    given(parsed = Parsed.error("error"));
    given(parsed = invoke(parsed, (result) -> result));
    when(parsed.errors());
    thenReturned(asList("error"));
  }

  @Test
  public void bifunction_invoke_with_two_results() throws Exception {
    given(parsed1 = parsed("one"));
    given(parsed2 = parsed("two"));
    given(parsed = invoke(parsed1, parsed2, (r1, r2) -> r1 + r2));
    when(parsed.result());
    thenReturned("onetwo");
  }

  @Test
  public void bifunction_invoke_with_result_and_error() throws Exception {
    given(parsed1 = parsed("one"));
    given(parsed2 = error("error"));
    given(parsed = invoke(parsed1, parsed2, (r1, r2) -> null));
    when(parsed.errors());
    thenReturned(asList("error"));
  }

  @Test
  public void bifunction_invoke_with_error_and_result() throws Exception {
    given(parsed1 = error("error"));
    given(parsed2 = parsed("one"));
    given(parsed = invoke(parsed1, parsed2, (r1, r2) -> null));
    when(parsed.errors());
    thenReturned(asList("error"));
  }

  @Test
  public void bifunction_invoke_with_two_errors() throws Exception {
    given(parsed1 = error("error1"));
    given(parsed2 = error("error2"));
    given(parsed = invoke(parsed1, parsed2, (r1, r2) -> null));
    when(parsed.errors());
    thenReturned(asList("error1", "error2"));
  }

  @Test
  public void trifunction_invoke_with_three_results() throws Exception {
    given(parsed1 = parsed("one"));
    given(parsed2 = parsed("two"));
    given(parsed3 = parsed("three"));
    given(parsed = invoke(parsed1, parsed2, parsed3, (r1, r2, r3) -> r1 + r2 + r3));
    when(parsed.result());
    thenReturned("onetwothree");
  }

  @Test
  public void trifunction_invoke_with_result_result_error() throws Exception {
    given(parsed1 = parsed("one"));
    given(parsed2 = parsed("two"));
    given(parsed3 = error("error3"));
    given(parsed = invoke(parsed1, parsed2, parsed3, (r1, r2, r3) -> null));
    when(parsed.errors());
    thenReturned(asList("error3"));
  }

  @Test
  public void trifunction_invoke_with_result_error_result() throws Exception {
    given(parsed1 = parsed("one"));
    given(parsed2 = error("error2"));
    given(parsed3 = parsed("three"));
    given(parsed = invoke(parsed1, parsed2, parsed3, (r1, r2, r3) -> null));
    when(parsed.errors());
    thenReturned(asList("error2"));
  }

  @Test
  public void trifunction_invoke_with_result_error_error() throws Exception {
    given(parsed1 = parsed("one"));
    given(parsed2 = error("error2"));
    given(parsed3 = error("error3"));
    given(parsed = invoke(parsed1, parsed2, parsed3, (r1, r2, r3) -> null));
    when(parsed.errors());
    thenReturned(asList("error2", "error3"));
  }

  @Test
  public void trifunction_invoke_with_error_result_result() throws Exception {
    given(parsed1 = error("error1"));
    given(parsed2 = parsed("two"));
    given(parsed3 = parsed("one"));
    given(parsed = invoke(parsed1, parsed2, parsed3, (r1, r2, r3) -> null));
    when(parsed.errors());
    thenReturned(asList("error1"));
  }

  @Test
  public void trifunction_invoke_with_error_result_error() throws Exception {
    given(parsed1 = error("error1"));
    given(parsed2 = parsed("two"));
    given(parsed3 = error("error3"));
    given(parsed = invoke(parsed1, parsed2, parsed3, (r1, r2, r3) -> null));
    when(parsed.errors());
    thenReturned(asList("error1", "error3"));
  }

  @Test
  public void trifunction_invoke_with_error_error_result() throws Exception {
    given(parsed1 = error("error1"));
    given(parsed2 = error("error2"));
    given(parsed3 = parsed("two"));
    given(parsed = invoke(parsed1, parsed2, parsed3, (r1, r2, r3) -> null));
    when(parsed.errors());
    thenReturned(asList("error1", "error2"));
  }

  @Test
  public void trifunction_invoke_with_error_error_error() throws Exception {
    given(parsed1 = error("error1"));
    given(parsed2 = error("error2"));
    given(parsed3 = error("error3"));
    given(parsed = invoke(parsed1, parsed2, parsed3, (r1, r2, r3) -> null));
    when(parsed.errors());
    thenReturned(asList("error1", "error2", "error3"));
  }
}
