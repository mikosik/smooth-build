package org.smoothbuild.acceptance.cli;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class NoCommandTest extends AcceptanceTestCase {
  @Test
  public void calling_smooth_without_command_defaults_to_help_command() {
    whenSmoothHelp();
    String helpOutput = sysOut();
    whenSmooth();
    thenFinishedWithSuccess();
    assertThat(sysOut())
        .isEqualTo(helpOutput);
  }

  @Test
  public void unknown_command() {
    whenSmoothHelp();
    whenSmooth("unknownCommand");
    thenSysErrContains(
        "Unmatched argument at index 0: 'unknownCommand'",
        "",
        "Did you mean: clean?",
        "Usage:",
        "smooth COMMAND",
        "Try 'smooth help' for more information.",
        "");
    thenFinishedWithError();
  }
}
