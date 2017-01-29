package org.smoothbuild.acceptance.cmd;

import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class BuildCommandTest extends AcceptanceTestCase {
  private String path;

  @Test
  public void build_command_fails_when_script_file_is_missing() throws Exception {
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenEqual(output(), "error: Cannot read build script file 'build.smooth'. "
        + "File 'build.smooth' doesn't exist.\n");
  }

  @Test
  public void build_command_without_function_argument_prints_error() throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild();
    thenFinishedWithError();
    thenEqual(output(), "error: Specify at least one function to be executed.\n");
  }

  @Test
  public void build_command_with_nonexistent_function_argument_prints_error() throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild("nonexistentFunction");
    thenFinishedWithError();
    thenEqual(output(), "error: Unknown function 'nonexistentFunction'.\n");
  }

  @Test
  public void temp_file_is_deleted_after_build_execution() throws Exception {
    givenScript("result = tempFilePath();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    thenEqual(new File(artifactContent("result")).exists(), false);
  }

  @Test
  public void smooth_temp_dir_is_deleted_before_build_starts() throws Exception {
    given(path = TEMPORARY_PATH.value() + "/file.txt");
    givenFile(path, "");
    givenScript("syntactically incorrect script");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenEqual(file(path).exists(), false);
  }

  @Test
  public void build_command_with_illegal_function_name_prints_error() throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild("illegal^name");
    thenFinishedWithError();
    thenEqual(output(), "error: Illegal function name 'illegal^name' passed in command line.\n");
  }

  @Test
  public void build_command_with_function_specified_twice_prints_error() throws Exception {
    givenScript("result = 'abc';");
    whenSmoothBuild("result", "result");
    thenFinishedWithError();
    thenEqual(output(), "error: Function 'result' has been specified more than once.\n");
  }

  public void build_command_clears_temporary_dir() throws Exception {
    givenFile(TEMPORARY_PATH.append(path("file")).value(), "content");
    givenScript("result = 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(!file(TEMPORARY_PATH.append(path("file")).value()).exists());
  }
}
