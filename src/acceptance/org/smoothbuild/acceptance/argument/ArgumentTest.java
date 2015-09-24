package org.smoothbuild.acceptance.argument;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ArgumentTest extends AcceptanceTestCase {
  @Test
  public void trailing_comma_in_argument_list() throws IOException {
    givenBuildScript(script("result : toBlob(string='abc',) ;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
  }

  @Test
  public void arguments_can_be_passed_in_the_same_order_as_parameters() throws Exception {
    givenBuildScript(script("result : twoStrings(stringA='abc', stringB='def') ;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc:def"));
  }

  @Test
  public void arguments_can_be_passed_in_reversed_order_of_parameters() throws Exception {
    givenBuildScript(script("result : twoStrings(stringB='def', stringA='abc') ;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc:def"));
  }
}
