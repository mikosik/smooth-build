package org.smoothbuild.acceptance.argument;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class MixedAssignmentTest extends AcceptanceTestCase {
  @Test
  public void assigns_nameless_to_required_parameter_even_when_not_required_parameter_matches()
      throws Exception {
    givenScript("oneOptionalOneRequired(String required, String optional = 'def') = required;"
        + "      result = oneOptionalOneRequired('abc');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void assigns_nameless_to_matching_parameter_that_was_left_once_named_was_assigned()
      throws Exception {
    givenScript("twoStrings(String a, String b) = b;"
        + "      result = twoStrings(a='abc', 'def');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("def"));
  }
}
