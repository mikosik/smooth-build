package org.smoothbuild.acceptance.cmd;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.testory.Testory.thenEqual;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CleanCommandTest extends AcceptanceTestCase {
  @Test
  public void clean_command_deletes_smooth_directory() throws Exception {
    givenScript("result: 'abc';");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    whenSmoothClean();
    thenFinishedWithSuccess();
    thenEqual(new File(projectDir(), SMOOTH_DIR.toString()).exists(), false);
  }

  @Test
  public void clean_command_with_arguments_prints_error() throws Exception {
    givenScript("result: 'abc';");
    whenSmoothClean("some arguments");
    thenFinishedWithError();
    thenEqual(output(), unknownArguments());
  }

  private static String unknownArguments() {
    StringBuilder builder = new StringBuilder();
    builder.append(" + CLEAN\n");
    builder.append("   + ERROR: Unknown arguments: [some, arguments]\n");
    builder.append(" + FAILED :(\n");
    builder.append("   + 1 error(s)\n");
    return builder.toString();
  }
}
