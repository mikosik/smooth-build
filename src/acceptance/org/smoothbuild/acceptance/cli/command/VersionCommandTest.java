package org.smoothbuild.acceptance.cli.command;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.install.BuildVersion;

@SuppressWarnings("ClassCanBeStatic")
public class VersionCommandTest extends AcceptanceTestCase {
  @Test
  public void version_command_prints_tool_version() {
    whenSmoothVersion();
    thenFinishedWithSuccess();
    thenSysOutContains("smooth build version " + BuildVersion.VERSION + "\n");
  }

  @Test
  public void version_command_prints_file_hashes() {
    whenSmoothVersion();
    thenFinishedWithSuccess();
    String hexNumberPattern = "[a-f0-9]+";
    assertThat(sysOut()).containsMatch("sandbox *" + hexNumberPattern);
    assertThat(sysOut()).containsMatch("  smooth.jar *" + hexNumberPattern);
    assertThat(sysOut()).containsMatch("  java platform *" + hexNumberPattern);
    assertThat(sysOut()).containsMatch("standard libraries *" + hexNumberPattern);
    assertThat(sysOut()).containsMatch("\\{SL}/slib.smooth *" + hexNumberPattern);
    thenSysOutContains("smooth build version " + BuildVersion.VERSION + "\n");
  }

  @Nested
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      whenSmooth("version", option);
    }
  }
}
