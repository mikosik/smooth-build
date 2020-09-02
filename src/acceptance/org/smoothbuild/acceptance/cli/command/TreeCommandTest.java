package org.smoothbuild.acceptance.cli.command;

import static org.smoothbuild.acceptance.CommandWithArgs.treeCommand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;
import org.smoothbuild.acceptance.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.acceptance.cli.command.common.LockFileTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.acceptance.cli.command.common.ValuesArgTestCase;
import org.smoothbuild.cli.command.TreeCommand;

public class TreeCommandTest {
  @Nested
  class basic extends AcceptanceTestCase {
    @Test
    public void with_parameter_and_array () throws Exception {
      createUserModule("""
              mySingleton(String element) = [element, "def"];
              result = mySingleton("abc");
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          [String] result
            [String] mySingleton()
              [String] [String]
                String "abc"
                String "def"
              """);
    }

    @Test
    public void with_long_string_literal () throws Exception {
      createUserModule("""
              result = '01234567890123456789012345678901234567890123456789';
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
              String result
                String "01234567890123456789012345678901234"...
              """);
    }

    @Test
    public void with_long_blob_literal () throws Exception {
      createUserModule("""
              result = 0x01234567890123456789012345678901234567890123456789;
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
              Blob result
                Blob 0x01234567890123456789012345678901234...
              """);
    }

    @Test
    public void with_convert_computation() throws Exception {
      createUserModule("""
              Blob result = file(toBlob("abc"), "name.txt");
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
              Blob result
                Blob Blob<-File
                  File file()
                    Blob toBlob()
                      String "abc"
                    String "name.txt"
              """);
    }
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return treeCommand("result");
    }
  }

  @Nested
  class LockFile extends LockFileTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return treeCommand("result");
    }
  }

  @Nested
  class FunctionArgs extends ValuesArgTestCase {
    @Override
    protected String commandName() {
      return TreeCommand.NAME;
    }

    @Override
    protected String sectionName() {
      return "Generating tree";
    }
  }

  @Nested
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(treeCommand(option, "result"));
    }
  }
}
