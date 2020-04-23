package org.smoothbuild.acceptance.cmd;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.SmoothConstants;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class VersionCommandTest extends AcceptanceTestCase {
  @Test
  public void version_command_prints_tool_version() {
    whenSmoothVersion();
    thenFinishedWithSuccess();
    thenSysOutContains("smooth build version " + SmoothConstants.VERSION + "\n");
  }

  @Test
  public void version_command_prints_file_hashes() {
    whenSmoothVersion();
    thenFinishedWithSuccess();
    String hexNumberPattern = "[a-f0-9]+";
    assertThat(sysOut()).containsMatch("sandbox *" + hexNumberPattern);
    assertThat(sysOut()).containsMatch("  smooth.jar *" + hexNumberPattern);
    assertThat(sysOut()).containsMatch("  java platform *" + hexNumberPattern);
    assertThat(sysOut()).containsMatch("slib.jar *" + hexNumberPattern);
    thenSysOutContains("smooth build version " + SmoothConstants.VERSION + "\n");
  }
}
