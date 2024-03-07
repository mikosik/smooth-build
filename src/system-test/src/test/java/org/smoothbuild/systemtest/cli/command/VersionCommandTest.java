package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.systemtest.CommandWithArgs.versionCommand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.app.layout.BuildVersion;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.systemtest.cli.command.common.AbstractLogLevelOptionTestSuite;

public class VersionCommandTest {
  @Nested
  class basic extends SystemTestCase {
    @Test
    public void version_command_prints_tool_version() {
      runSmoothVersion();
      assertFinishedWithSuccess();
      assertSystemOutContains("smooth build version " + BuildVersion.VERSION + "\n");
    }

    @Test
    public void version_command_prints_file_hashes() {
      runSmoothVersion();
      assertFinishedWithSuccess();
      String hexNumberPattern = "[a-f0-9]+";
      assertThat(systemOut()).containsMatch("installation +" + hexNumberPattern);
      assertThat(systemOut()).containsMatch("  smooth.jar +" + hexNumberPattern);
      assertThat(systemOut()).containsMatch("  standard libraries +" + hexNumberPattern);
      assertThat(systemOut()).containsMatch("    \\{ssl}/std_lib.smooth +" + hexNumberPattern);
      assertSystemOutContains("smooth build version " + BuildVersion.VERSION + "\n");
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
