package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestContext;

public class NoCommandTest extends SystemTestContext {
  @Test
  void calling_smooth_without_command_defaults_to_help_command() {
    var helpOutput = runSmoothHelp();
    String helpSysOut = helpOutput.systemOut();
    var smoothOutput = runSmoothWithoutProjectAndInstallationDir(new CommandWithArgs());
    smoothOutput.assertFinishedWithSuccess();
    assertThat(smoothOutput.systemOut()).isEqualTo(helpSysOut);
  }
}
