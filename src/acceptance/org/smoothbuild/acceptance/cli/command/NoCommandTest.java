package org.smoothbuild.acceptance.cli.command;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;

public class NoCommandTest extends AcceptanceTestCase {
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
