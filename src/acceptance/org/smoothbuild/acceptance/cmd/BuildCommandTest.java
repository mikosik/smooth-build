package org.smoothbuild.acceptance.cmd;

import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.lang.nativ.TempFilePath;

public class BuildCommandTest extends AcceptanceTestCase {
  private String path;

  @Test
  public void build_command_fails_when_script_file_is_missing() throws Exception {
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("error: Cannot read build script file 'build.smooth'.\n");
  }

  @Test
  public void build_command_without_function_argument_prints_error() throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild();
    thenFinishedWithError();
    thenOutputContains("error: Specify at least one function to be executed.\n"
        + "Use 'smooth list' to see all available functions.\n");
  }

  @Test
  public void build_command_with_nonexistent_function_argument_prints_error() throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild("nonexistentFunction");
    thenFinishedWithError();
    thenOutputContains("error: Unknown function 'nonexistentFunction'.\n"
        + "Use 'smooth list' to see all available functions.\n");
  }

  @Test
  public void temp_file_is_deleted_after_build_execution() throws Exception {
    givenNativeJar(TempFilePath.class);
    givenScript("String tempFilePath();"
        + "      result = tempFilePath();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(new File(artifactContent("result")).exists(), false);
  }

  @Test
  public void build_command_with_illegal_function_name_prints_error() throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild("illegal^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
  }

  @Test
  public void build_command_with_illegal_function_names_prints_error_for_each_one()
      throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild("illegal^name other^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
    thenOutputContains("error: Illegal function name 'other^name' passed in command line.\n");
  }

  @Test
  public void build_command_with_function_specified_twice_prints_error() throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild("result", "result");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
  }

  @Test
  public void build_command_with_many_functions_specified_twice_prints_error_for_each_one()
      throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild("result", "result", "other", "other");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
    thenOutputContains("error: Function 'other' has been specified more than once.\n");
  }

  @Test
  public void build_command_clears_artifacts_dir() throws Exception {
    given(path = ARTIFACTS_PATH.value() + "/file.txt");
    givenFile(path, "content");
    givenScript("syntactically incorrect script");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(!file(path).exists());
  }

  @Test
  public void build_command_clears_temporary_dir() throws Exception {
    given(path = TEMPORARY_PATH.value() + "/file.txt");
    givenFile(path, "content");
    givenScript("syntactically incorrect script");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(!file(path).exists());
  }
}
