package org.smoothbuild.acceptance.cmd;

import static org.testory.Testory.given;
import static org.testory.Testory.thenEqual;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class GenericCommandTest extends AcceptanceTestCase {
  private String helpOutput;

  @Test
  public void calling_smooth_without_command_defaults_to_help_command() throws Exception {
    whenSmoothHelp();
    given(helpOutput = output());
    whenSmooth();
    thenReturnedCode(0);
    thenEqual(output(), helpOutput);
  }

  @Test
  public void unknown_command() throws Exception {
    whenSmoothHelp();
    whenSmooth("unknownCommand");
    thenReturnedCode(2);
    thenEqual(output(), "smooth: 'unknownCommand' is not a smooth command. See 'smooth help'.\n");
  }
}
