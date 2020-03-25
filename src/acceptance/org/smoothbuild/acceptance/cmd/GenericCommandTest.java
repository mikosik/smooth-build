package org.smoothbuild.acceptance.cmd;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class GenericCommandTest extends AcceptanceTestCase {
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
    thenFinishedWithError();
    thenOutputContains("smooth: 'unknownCommand' is not a smooth command. See 'smooth help'.\n");
  }
}
