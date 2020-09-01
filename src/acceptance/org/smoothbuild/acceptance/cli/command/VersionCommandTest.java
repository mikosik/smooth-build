package org.smoothbuild.acceptance.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.acceptance.CommandWithArgs.versionCommand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.install.BuildVersion;

public class VersionCommandTest {
  @Nested
  class basic extends AcceptanceTestCase {
    @Test
    public void version_command_prints_tool_version() {
      runSmoothVersion();
      assertFinishedWithSuccess();
      assertSysOutContains("smooth build version " + BuildVersion.VERSION + "\n");
    }

    @Test
    public void version_command_prints_file_hashes() {
      runSmoothVersion();
      assertFinishedWithSuccess();
      String hexNumberPattern = "[a-f0-9]+";
      assertThat(sysOut()).containsMatch("sandbox *" + hexNumberPattern);
      assertThat(sysOut()).containsMatch("  smooth.jar *" + hexNumberPattern);
      assertThat(sysOut()).containsMatch("  java platform *" + hexNumberPattern);
      assertThat(sysOut()).containsMatch("standard libraries *" + hexNumberPattern);
      assertThat(sysOut()).containsMatch("\\{slib}/slib.smooth *" + hexNumberPattern);
      assertSysOutContains("smooth build version " + BuildVersion.VERSION + "\n");
    }
  }

  @Nested
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(versionCommand(option));
    }
  }
}
