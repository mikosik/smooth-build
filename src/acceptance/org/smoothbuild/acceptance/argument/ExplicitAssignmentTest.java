package org.smoothbuild.acceptance.argument;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ExplicitAssignmentTest extends AcceptanceTestCase {
  @Test
  public void fails_when_parameter_with_given_name_doesnt_exist() throws Exception {
    givenScript("result : stringIdentity(wrongName='abc');");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString("Function 'stringIdentity' has no parameter named 'wrongName'."));
  }

  @Test
  public void fails_when_parameter_has_incompatible_type() throws Exception {
    givenScript("result : blobIdentity(blob='abc');");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), containsString("Type mismatch, cannot convert String to Blob"));
  }

  @Test
  public void assigns_to_parameter_with_same_type() throws Exception {
    givenScript("result : stringIdentity(string='abc');");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void assigns_to_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result : blobIdentity(blob=file('file.txt'));");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void assigns_nil_to_string_array() throws Exception {
    givenScript("result : stringArrayIdentity(stringArray=[]) ;");
    whenSmoothBuild("result");
    thenReturnedCode(0);
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void fails_when_two_arguments_are_assigned_to_same_parameter() throws Exception {
    givenScript("result : stringIdentity(string='abc', string='def');");
    whenSmoothBuild("result");
    thenReturnedCode(2);
    then(output(), Matchers.containsString("Duplicated argument name = string"));
  }
}
