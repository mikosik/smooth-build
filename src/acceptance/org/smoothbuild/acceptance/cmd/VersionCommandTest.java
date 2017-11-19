package org.smoothbuild.acceptance.cmd;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.SmoothConstants;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class VersionCommandTest extends AcceptanceTestCase {
  @Test
  public void version_command_prints_tool_version() throws Exception {
    whenSmoothVersion();
    thenFinishedWithSuccess();
    then(output(), containsString("smooth build version " + SmoothConstants.VERSION + "\n"));
  }
}
