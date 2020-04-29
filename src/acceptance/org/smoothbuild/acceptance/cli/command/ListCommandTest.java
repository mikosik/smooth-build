package org.smoothbuild.acceptance.cli.command;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.cli.ListCommand;

@SuppressWarnings("ClassCanBeStatic")
public class ListCommandTest extends AcceptanceTestCase {
  @Test
  public void list_command_lists_all_available_functions() throws Exception {
    givenScript(
        "  bFunction = 'abc';  ",
        "  aFunction = 'abc';  ",
        "  dFunction = 'abc';  ",
        "  cFunction = 'abc';  ");
    whenSmoothList();
    thenFinishedWithSuccess();
    thenSysOutContains(
        "aFunction",
        "bFunction",
        "cFunction",
        "dFunction",
        "");
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected String[] commandNameWithArgument() {
      return new String[] { ListCommand.NAME };
    }
  }

  @Nested
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      whenSmooth("list", option, "result");
    }
  }
}
