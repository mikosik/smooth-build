package org.smoothbuild.acceptance.argument;

import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ExplicitAssignmentTest extends AcceptanceTestCase {
  @Test
  public void fails_when_parameter_with_given_name_doesnt_exist() throws Exception {
    givenScript("result : stringIdentity(wrongName='abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenEqual(output(),
        "build.smooth:1: error: Function 'stringIdentity' has no parameter 'wrongName'.\n");
  }

  @Test
  public void fails_when_parameter_has_incompatible_type() throws Exception {
    givenScript("result : blobIdentity(blob='abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenEqual(output(),
        "build.smooth:1: error: Type mismatch, cannot convert argument 'blob' of type 'String' to 'Blob'.\n");
  }

  @Test
  public void assigns_to_parameter_with_same_type() throws Exception {
    givenScript("result : stringIdentity(string='abc');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void assigns_to_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result : blobIdentity(blob=file('file.txt'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void assigns_nil_to_string_array() throws Exception {
    givenScript("result : stringArrayIdentity(stringArray=[]) ;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void fails_when_two_arguments_are_assigned_to_same_parameter() throws Exception {
    givenScript("result : stringIdentity(string='abc', string='def');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenEqual(output(), "build.smooth:1: error: Argument 'string' assigned twice.\n");
  }
}
