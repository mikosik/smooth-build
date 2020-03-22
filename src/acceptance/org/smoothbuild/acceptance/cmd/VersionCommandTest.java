package org.smoothbuild.acceptance.cmd;

import org.junit.jupiter.api.Test;
import org.smoothbuild.SmoothConstants;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class VersionCommandTest extends AcceptanceTestCase {
  @Test
  public void version_command_prints_tool_version() throws Exception {
    whenSmoothVersion();
    thenFinishedWithSuccess();
    thenOutputContains("smooth build version " + SmoothConstants.VERSION + "\n");
  }
}
