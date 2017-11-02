package org.smoothbuild.acceptance.argument;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArgumentTest extends AcceptanceTestCase {
  @Test
  public void trailing_comma_in_argument_list() throws IOException {
    givenScript("func(String string) = string;"
        + "      result = func(string='abc',) ;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void arguments_can_be_passed_in_the_same_order_as_parameters() throws Exception {
    givenScript("returnFirst(String a, String b) = a;"
        + "      result = returnFirst(a='abc', b='def') ;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void arguments_can_be_passed_in_reversed_order_of_parameters() throws Exception {
    givenScript("returnFirst(String a, String b) = a;"
        + "result = returnFirst(b='def', a='abc') ;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }
}
