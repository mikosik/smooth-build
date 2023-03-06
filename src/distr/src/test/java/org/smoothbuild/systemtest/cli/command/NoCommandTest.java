package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestCase;

public class NoCommandTest extends SystemTestCase {
  @Test
  public void calling_smooth_without_command_defaults_to_help_command() {
    runSmoothHelp();
    String helpOutput = sysOut();
    runSmoothWithoutProjectAndInstallationDir(new CommandWithArgs(""));
    assertFinishedWithSuccess();
    assertThat(sysOut())
        .isEqualTo(helpOutput);
  }
}
