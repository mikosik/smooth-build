package org.smoothbuild.acceptance.argument;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.ArrayMatcher.isArrayWith;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ExplicitAssignmentTest extends AcceptanceTestCase {
  @Test
  public void fails_when_parameter_with_given_name_doesnt_exist() throws Exception {
    givenScript("func(String string) = string;"
        + "      result = func(wrongName='abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Function 'func' has no parameter 'wrongName'.\n"));
  }

  @Test
  public void fails_when_parameter_has_incompatible_type() throws Exception {
    givenScript("func(Blob blob) = blob;"
        + "      result = func(blob='abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("build.smooth:1: error: "
        + "Type mismatch, cannot convert argument 'blob' of type 'String' to 'Blob'.\n"));
  }

  @Test
  public void assigns_to_parameter_with_same_type() throws Exception {
    givenScript("func(String string) = string;"
        + "      result = func(string='abc');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void assigns_to_parameter_with_supertype() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("func(Blob blob) = blob;"
        + "      result = func(blob=file('//file.txt'));");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }

  @Test
  public void assigns_nil_to_string_array() throws Exception {
    givenScript("func([String] array) = array;"
        + "      result = func(array=[]) ;");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), isArrayWith());
  }

  @Test
  public void fails_when_two_arguments_are_assigned_to_same_parameter() throws Exception {
    givenScript("func(String string) = string;"
        + "      result = func(string='abc', string='def');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Argument 'string' assigned twice.\n"));
  }
}
