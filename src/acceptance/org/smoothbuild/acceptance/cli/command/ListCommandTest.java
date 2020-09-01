package org.smoothbuild.acceptance.cli.command;

import static org.smoothbuild.acceptance.CommandWithArgs.listCommand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;
import org.smoothbuild.acceptance.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.acceptance.cli.command.common.LockFileTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;

public class ListCommandTest {
  @Nested
  class basic extends AcceptanceTestCase {
    @Test
    public void list_command_lists_all_available_functions() throws Exception {
      createUserModule(
          "  bFunction = 'abc';  ",
          "  aFunction = 'abc';  ",
          "  dFunction = 'abc';  ",
          "  cFunction = 'abc';  ");
      runSmoothList();
      assertFinishedWithSuccess();
      assertSysOutContains(
          "aFunction",
          "bFunction",
          "cFunction",
          "dFunction",
          "");
    }
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return listCommand();
    }
  }

  @Nested
  class LockFile extends LockFileTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return listCommand();
    }
  }

  @Nested
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(listCommand(option, "result"));
    }
  }
}
