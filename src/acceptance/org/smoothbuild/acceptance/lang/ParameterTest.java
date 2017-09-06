package org.smoothbuild.acceptance.lang;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;

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
  public void string_parameter_can_be_declared() throws Exception {
    givenScript("oneParameter(String string) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void blob_parameter_can_be_declared() throws Exception {
    givenScript("oneParameter(Blob blob) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void file_parameter_can_be_declared() throws Exception {
    givenScript("oneParameter(File file) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void nothing_parameter_can_be_declared() throws Exception {
    givenScript("oneParameter(Nothing nothing) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void unknown_type_parameter_cannot_be_declared() throws Exception {
    givenScript("oneParameter(Unknown unknown) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Unknown type 'Unknown'.\n"));
  }

  @Test
  public void string_array_parameter_can_be_declared() throws Exception {
    givenScript("oneParameter([String] array) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void blob_array_parameter_can_be_declared() throws Exception {
    givenScript("oneParameter([Blob] array) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void file_array_parameter_can_be_declared() throws Exception {
    givenScript("oneParameter([File] array) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void nothing_array_parameter_can_be_declared() throws Exception {
    givenScript("oneParameter([Nothing] array) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
  }

  @Test
  public void unknown_array_type_parameter_cannot_be_declared() throws Exception {
    givenScript("oneParameter([Unknown] unknown) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Unknown type 'Unknown'.\n"));
  }

  @Test
  public void nested_array_parameter_type_cannot_be_declared() throws Exception {
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

  @Test
  public void parameter_with_unknown_type_is_forbidden() throws Exception {
    givenScript("func(Unknown string) = 'abc';"
        + "result = 'def';");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Unknown type 'Unknown'.\n"));
  }

  @Test
  public void calling_defined_function_with_one_parameter() throws Exception {
    givenScript("func(String string) = 'abc';"
        + "result = func('def');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void defined_function_that_returns_parameter() throws Exception {
    givenScript("func(String string) = string;"
        + "result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void argument_is_not_evaluated_when_assigned_to_not_used_parameter() throws Exception {
    givenScript("func(String notUsedParameter) = 'abc';"
        + "result = func(throwFileSystemException());");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void calling_parameter_as_function_causes_error() throws Exception {
    givenScript("func(String param) = param();"
        + "      result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "build.smooth:1: error: Parameter 'param' cannot be called as it is not a function.\n"));
  }

  @Test
  public void parameter_can_shadow_builtin_function() throws Exception {
    givenScript("func(String zip) = zip;"
        + "      result = func('abc');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "abc");
  }

  @Test
  public void parameter_can_shadow_function() throws Exception {
    givenScript("func1 = 'abc';"
        + "      func2(String func) = func;"
        + "      result = func2('def');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(artifactContent("result"), "def");
  }
}
