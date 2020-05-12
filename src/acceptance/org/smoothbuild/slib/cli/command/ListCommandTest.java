package org.smoothbuild.slib.cli.command;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.cli.command.ListCommand;
import org.smoothbuild.slib.AcceptanceTestCase;
import org.smoothbuild.slib.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.slib.cli.command.common.LogLevelOptionTestCase;

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
