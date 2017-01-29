package org.smoothbuild.acceptance.argument;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArgumentTest extends AcceptanceTestCase {
  @Test
  public void trailing_comma_in_argument_list() throws IOException {
    givenScript("result = toBlob(string='abc',) ;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void arguments_can_be_passed_in_the_same_order_as_parameters() throws Exception {
    givenScript("result = twoStrings(stringA='abc', stringB='def') ;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc:def"));
  }

  @Test
  public void arguments_can_be_passed_in_reversed_order_of_parameters() throws Exception {
    givenScript("result = twoStrings(stringB='def', stringA='abc') ;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc:def"));
  }
}
