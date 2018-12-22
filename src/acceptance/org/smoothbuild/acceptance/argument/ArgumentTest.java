package org.smoothbuild.acceptance.argument;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArgumentTest extends AcceptanceTestCase {
  @Test
  public void trailing_comma_in_argument_list() throws IOException {
    givenScript("func(String string) = string;     \n"
        + "      result = func(string='abc',);     \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void arguments_can_be_passed_in_the_same_order_as_parameters() throws Exception {
    givenScript("returnFirst(String a, String b) = a;     \n"
        + "      result = returnFirst(a='abc', b='def');  \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void arguments_can_be_passed_in_reversed_order_of_parameters() throws Exception {
    givenScript("returnFirst(String a, String b) = a;      \n"
        + "      result = returnFirst(b='def', a='abc');   \n");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void all_named_arguments_must_come_after_positional() throws Exception {
    givenScript("returnFirst(String a, String b) = a;      \n"
        + "      result = returnFirst(b='def', 'abc');     \n");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContainsError(2, "Named arguments must be placed after all positional arguments.\n");
  }
}
