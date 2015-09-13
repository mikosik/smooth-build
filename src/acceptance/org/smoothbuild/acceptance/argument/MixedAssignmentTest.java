package org.smoothbuild.acceptance.argument;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class MixedAssignmentTest extends AcceptanceTestCase {
  @Test
  public void assigns_nameless_to_required_parameter_even_when_not_required_parameter_matches()
      throws Exception {
    givenBuildScript(script("result: oneOptionalOneRequired('abc');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent(":abc"));
  }

  @Test
  public void assigns_nameless_to_matching_parameter_that_was_left_once_named_was_assigned()
      throws Exception {
    givenBuildScript(script("result: twoStrings(stringA='abc', 'def');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc:def"));
  }
}
