package org.smoothbuild.acceptance.cmd;

import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class BuildCommandTest extends AcceptanceTestCase {
  @Test
  public void build_command_without_function_argument_prints_error() throws Exception {
    givenScript("result: 'abc';");
    whenSmoothBuild();
    thenFinishedWithError();
    thenEqual(output(), noFunctionArgError());
  }

  private static String noFunctionArgError() {
    StringBuilder builder = new StringBuilder();
    builder.append(" + SMOOTH EXECUTOR\n");
    builder.append("   + ERROR: No function passed to build command.\n");
    builder.append("     Pass at least one from following available functions:\n");
    builder.append("       'result'\n");
    builder.append(" + FAILED :(\n");
    builder.append("   + 1 error(s)\n");
    return builder.toString();
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
}
