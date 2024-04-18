package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.systemtest.CommandWithArgs.versionCommand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.cli.layout.BuildVersion;
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
      var systemOutWithReplacedHashes = systemOut().replaceAll("[0-9a-f]{64}", "HASH");
      assertThat(systemOutWithReplacedHashes)
          .contains(
              """
          installation HASH
            smooth.jar HASH
            standard libraries HASH
              {library}/std_lib module HASH
                {library}/std_lib.smooth HASH
                {library}/std_lib.jar HASH
        """);
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
