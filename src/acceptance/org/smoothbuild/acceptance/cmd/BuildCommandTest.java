package org.smoothbuild.acceptance.cmd;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class BuildCommandTest extends AcceptanceTestCase {

  @Test
  public void build_command_fails_when_script_file_is_missing() throws Exception {
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenEqual(output(), "error: Cannot find build script file 'build.smooth'.\n");
  }

  @Test
  public void build_command_without_function_argument_prints_error() throws Exception {
    givenScript("result: 'abc';");
    whenSmoothBuild();
    thenFinishedWithError();
    thenEqual(output(), "error: No function passed to build command.\n"
        + "  Pass at least one from following available functions:\n"
        + "    'result'\n");
  }

  @Test
  public void build_command_with_nonexistent_function_argument_prints_error() throws Exception {
    givenScript("result: 'abc';");
    whenSmoothBuild("nonexistentFunction");
    thenFinishedWithError();
    thenEqual(output(), nonexistentFunctionArgError());
  }

  private String nonexistentFunctionArgError() {
    StringBuilder builder = new StringBuilder();
    builder.append(" + SMOOTH EXECUTOR\n");
    builder.append("   + ERROR: Unknown function 'nonexistentFunction' passed in command line.\n");
    builder.append("     Only following function(s) are available:\n");
    builder.append("       'result'\n");
    builder.append(" + FAILED :(\n");
    builder.append("   + 1 error(s)\n");
    return builder.toString();
  }

  @Test
  public void temp_directory_is_deleted_after_build_execution() throws Exception {
    givenScript("result: tempFilePath();");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    when(new File(artifactContent("result"))).exists();
    thenReturned(false);
  }

  @Test
  public void build_command_with_illegal_function_name_prints_error() throws Exception {
    givenScript("result: 'abc';");
    whenSmoothBuild("illegal^name");
    thenFinishedWithError();
    thenEqual(output(), "error: Illegal function name 'illegal^name' passed in command line.\n");
  }

  @Test
  public void build_command_with_function_specified_twice_prints_error() throws Exception {
    givenScript("result: 'abc';");
    whenSmoothBuild("result", "result");
    thenFinishedWithError();
    thenEqual(output(), "error: Function 'result' has been specified more than once.\n");
  }

  @Test
  public void storing_array_with_two_files_with_same_path_prints_error() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result: [file('file.txt'), file('file.txt')];");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Can't store result of 'result' as it contains files with duplicated paths:\n"
            + "  'file.txt'\n"));
  }
}
