package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class ParameterTest extends AcceptanceTestCase {

  @Test
  public void no_parameters() throws Exception {
    givenScript("noParameters() = 'abc';"
        + "result() = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void string_parameter() throws Exception {
    givenScript("oneParameter(String string) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void blob_parameter() throws Exception {
    givenScript("oneParameter(Blob blob) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void file_parameter() throws Exception {
    givenScript("oneParameter(File file) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void nothing_parameter() throws Exception {
    givenScript("oneParameter(Nothing nothing) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void unknown_type_parameter() throws Exception {
    givenScript("oneParameter(Unknown unknown) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Unknown type 'Unknown'.\n"));
  }

  @Test
  public void string_array_parameter() throws Exception {
    givenScript("oneParameter([String] array) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void blob_array_parameter() throws Exception {
    givenScript("oneParameter([Blob] array) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void file_array_parameter() throws Exception {
    givenScript("oneParameter([File] array) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void nothing_array_parameter() throws Exception {
    givenScript("oneParameter([Nothing] array) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void unknown_array_type_parameter() throws Exception {
    givenScript("oneParameter([Unknown] unknown) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Unknown type 'Unknown'.\n"));
  }

  @Test
  public void nested_array_parameter_type_is_forbidden() throws Exception {
    givenScript("oneParameter([[String]] array) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Nested array type is forbidden.\n"));
  }

  @Test
  public void two_parameters_with_same_name_are_forbidden() throws Exception {
    givenScript("twoParameters(String name, String name) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Duplicate parameter 'name'.\n"));
  }
}
