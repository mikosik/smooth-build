package org.smoothbuild.acceptance.clean;

import static org.smoothbuild.SmoothConstants.SMOOTH_DIR;
import static org.testory.Testory.thenEqual;

import java.io.File;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class CleanCommandTest extends AcceptanceTestCase {
  @Test
  public void clean_command_deletes_smooth_directory() throws Exception {
    givenBuildScript(script("result: 'abc';"));
    whenRunSmoothBuild("result");
    thenReturnedCode(0);
    whenRunSmoothClean();
    thenReturnedCode(0);
    thenEqual(new File(projectDir(), SMOOTH_DIR.toString()).exists(), false);
  }
}
