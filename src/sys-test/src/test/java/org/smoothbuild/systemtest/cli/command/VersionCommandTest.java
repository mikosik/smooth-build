package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.systemtest.CommandWithArgs.versionCommand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.filesystem.install.BuildVersion;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.systemtest.cli.command.common.AbstractLogLevelOptionTestSuite;

public class VersionCommandTest {
  @Nested
  class basic extends SystemTestCase {
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
      assertThat(sysOut()).containsMatch("installation *" + hexNumberPattern);
      assertThat(sysOut()).containsMatch("  smooth.jar *" + hexNumberPattern);
      assertThat(sysOut()).containsMatch("  standard libraries *" + hexNumberPattern);
      assertThat(sysOut()).containsMatch("    \\{std-lib}/std_lib.smooth *" + hexNumberPattern);
      assertSysOutContains("smooth build version " + BuildVersion.VERSION + "\n");
    }
  }

  @Nested
  class LogLevelOption extends AbstractLogLevelOptionTestSuite {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(versionCommand(option));
    }
  }
}
