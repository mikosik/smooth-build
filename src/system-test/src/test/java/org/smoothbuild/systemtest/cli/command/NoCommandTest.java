package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestContext;

public class NoCommandTest extends SystemTestContext {
  @Test
  void calling_smooth_without_command_defaults_to_help_command() {
    runSmoothHelp();
    String helpOutput = systemOut();
    runSmoothWithoutProjectAndInstallationDir(new CommandWithArgs(""));
    assertFinishedWithSuccess();
    assertThat(systemOut()).isEqualTo(helpOutput);
  }
}
