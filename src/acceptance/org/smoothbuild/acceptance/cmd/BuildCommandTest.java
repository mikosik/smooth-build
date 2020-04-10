package org.smoothbuild.acceptance.cmd;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;

import java.io.File;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.TempFilePath;

public class BuildCommandTest extends AcceptanceTestCase {
  @Test
  public void build_command_fails_when_script_file_is_missing() {
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("error: 'build.smooth' doesn't exist.\n");
  }

  @Test
  public void build_command_without_function_argument_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild();
    thenFinishedWithError();
    thenErrorContains(
        "Missing required parameter: <function>",
        "",
        "Usage:",
        "smooth build <function>...",
        "Try 'smooth help build' for more information.",
        "");
  }

  @Test
  public void build_command_with_function_that_requires_arguments_prints_error() throws Exception {
    givenScript(
        "  String testStringIdentity(String value) = value;  ");
    whenSmoothBuild("testStringIdentity");
    thenFinishedWithError();
    thenOutputContains("error: Function 'testStringIdentity' cannot be invoked from command line "
        + "as it requires arguments.\n");
  }

  @Test
  public void build_command_with_function_which_all_params_are_optional_is_allowed() throws Exception {
    givenScript(
        "  String testStringIdentity(String value = 'default') = value;  ");
    whenSmoothBuild("testStringIdentity");
    thenFinishedWithSuccess();
    assertThat(artifactContent("testStringIdentity"))
        .isEqualTo("default");
  }

  @Test
  public void build_command_with_nonexistent_function_argument_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("nonexistentFunction");
    thenFinishedWithError();
    thenOutputContains(
        "error: Unknown function 'nonexistentFunction'.",
        "Try 'smooth list' to see all available functions.\n");
  }

  @Test
  public void temp_file_is_deleted_after_build_execution() throws Exception {
    givenNativeJar(TempFilePath.class);
    givenScript(
        "  String tempFilePath();    ",
        "  result = tempFilePath();  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(new File(artifactContent("result")).exists())
        .isFalse();
  }

  @Test
  public void build_command_with_illegal_function_name_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("illegal^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
  }

  @Test
  public void build_command_with_illegal_function_names_prints_error_for_each_one()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("illegal^name other^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
    thenOutputContains("error: Illegal function name 'other^name' passed in command line.\n");
  }

  @Test
  public void build_command_with_function_specified_twice_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result", "result");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
  }

  @Test
  public void build_command_with_many_functions_specified_twice_prints_error_for_each_one()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothBuild("result", "result", "other", "other");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
    thenOutputContains("error: Function 'other' has been specified more than once.\n");
  }

  @Test
  public void build_command_clears_artifacts_dir() throws Exception {
    String path = ARTIFACTS_PATH.value() + "/file.txt";
    givenFile(path, "content");
    givenScript(
        "  syntactically incorrect script  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    assertThat(file(path).exists())
        .isFalse();
  }

  @Test
  public void build_command_clears_temporary_dir() throws Exception {
    String path = TEMPORARY_PATH.value() + "/file.txt";
    givenFile(path, "content");
    givenScript(
        "  syntactically incorrect script  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    assertThat(file(path).exists())
        .isFalse();
  }
}
