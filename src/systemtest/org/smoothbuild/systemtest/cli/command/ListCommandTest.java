package org.smoothbuild.systemtest.cli.command;

import static org.smoothbuild.systemtest.CommandWithArgs.listCommand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.systemtest.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.systemtest.cli.command.common.LockFileTestCase;
import org.smoothbuild.systemtest.cli.command.common.LogLevelOptionTestCase;

public class ListCommandTest {
  @Nested
  class basic extends SystemTestCase {
    @Test
    public void list_command_lists_all_available_values() throws Exception {
      createUserModule("""
            bValue = "abc";
            aValue = "abc";
            dValue = "abc";
            cValue = "abc";
              """);
      runSmoothList();
      assertFinishedWithSuccess();
      assertSysOutContains(
          "aValue",
          "bValue",
          "cValue",
          "dValue",
          "");
    }
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected CommandWithArgs commandNameWithArg() {
      return listCommand();
    }
  }

  @Nested
  class LockFile extends LockFileTestCase {
    @Override
    protected CommandWithArgs commandNameWithArg() {
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
