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
    thenOutputContains("smooth build version " + SmoothConstants.VERSION + "\n");
  }

  @Test
  public void version_command_prints_file_hashes() {
    whenSmoothVersion();
    thenFinishedWithSuccess();
    String hexNumberPattern = "[a-f0-9]+";
    assertThat(output()).containsMatch("sandbox *" + hexNumberPattern);
    assertThat(output()).containsMatch("  smooth.jar *" + hexNumberPattern);
    assertThat(output()).containsMatch("  java platform *" + hexNumberPattern);
    assertThat(output()).containsMatch("funcs.jar *" + hexNumberPattern);
    thenOutputContains("smooth build version " + SmoothConstants.VERSION + "\n");
  }
}
