package org.smoothbuild.acceptance.cmd;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class NoCommandTest extends AcceptanceTestCase {
  @Test
  public void calling_smooth_without_command_defaults_to_help_command() {
    whenSmoothHelp();
    String helpOutput = output();
    whenSmooth();
    thenFinishedWithSuccess();
    assertThat(output())
        .isEqualTo(helpOutput);
  }

  @Test
  public void unknown_command() {
    whenSmoothHelp();
    whenSmooth("unknownCommand");
    thenErrorContains(
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
