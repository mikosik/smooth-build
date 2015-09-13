package org.smoothbuild.acceptance.argument;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ExplicitAssignmentTest extends AcceptanceTestCase {
  @Test
  public void fails_when_parameter_with_given_name_doesnt_exist() throws Exception {
    givenBuildScript(script("result : stringIdentity(wrongName='abc');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Function 'stringIdentity' has no parameter named 'wrongName'."));
  }

  @Test
  public void fails_when_parameter_has_incompatible_type() throws Exception {
    givenBuildScript(script("result : blobIdentity(blob='abc');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(containsString("Type mismatch, cannot convert String to Blob"));
  }

  @Test
  public void assigns_to_parameter_with_same_type() throws Exception {
    givenBuildScript(script("result : stringIdentity(string='abc');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }

  @Test
  public void assigns_to_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenBuildScript(script("result : blobIdentity(blob=file('file.txt'));"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", hasContent("abc"));
  }

  @Test
  public void assigns_nil_to_string_array() throws Exception {
    givenBuildScript(script("result : stringArrayIdentity(stringArray=[]) ;"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    thenArtifact("result", isArrayWith());
  }

  @Test
  public void fails_when_two_arguments_are_assigned_to_same_parameter() throws Exception {
    givenBuildScript(script("result : stringIdentity(string='abc', string='def');"));
    whenRunSmoothBuild("result");
    thenReturnedCode(1);
    thenPrinted(Matchers.containsString("Duplicated argument name = string"));
  }
}
