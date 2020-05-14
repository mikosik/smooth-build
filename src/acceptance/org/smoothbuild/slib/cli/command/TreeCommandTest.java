package org.smoothbuild.slib.cli.command;

import static org.smoothbuild.util.Strings.unlines;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.cli.command.TreeCommand;
import org.smoothbuild.slib.AcceptanceTestCase;
import org.smoothbuild.slib.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.slib.cli.command.common.FunctionsArgTestCase;
import org.smoothbuild.slib.cli.command.common.LockFileTestCase;
import org.smoothbuild.slib.cli.command.common.LogLevelOptionTestCase;

@SuppressWarnings("ClassCanBeStatic")
public class TreeCommandTest {
  @Nested
  class basic extends AcceptanceTestCase {
    @Test
    public void with_parameter_and_array () throws Exception {
      givenScript(
          "  mySingleton(String element) = [element, 'def'];  ",
          "  result = mySingleton('abc');                     ");
      whenSmoothTree("result");
      thenFinishedWithSuccess();
      thenSysOutContains(quotesX2(unlines(
          "[String] result",
          "  [String] mySingleton",
          "    [String]",
          "      String 'abc'",
          "      String 'def'")));
    }

    @Test
    public void with_long_string_literal () throws Exception {
      givenScript(
          "  result = '01234567890123456789012345678901234567890123456789';  ");
      whenSmoothTree("result");
      thenFinishedWithSuccess();
      thenSysOutContains(quotesX2(unlines(
          "String result",
          "  String '01234567890123456789012345678901234'...",
          "")));
    }

    @Test
    public void with_convert_computation () throws Exception {
      givenScript(
          "  Blob result = file(toBlob('abc'), 'name.txt');  ");
      whenSmoothTree("result");
      thenFinishedWithSuccess();
      thenSysOutContains(quotesX2(unlines(
          "Blob result",
          "  Blob <- File",
          "    File file",
          "      Blob toBlob",
          "        String 'abc'",
          "      String 'name.txt'")));
    }
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected String[] commandNameWithArgument() {
      return new String[] { TreeCommand.NAME, "result" };
    }
  }

  @Nested
  class LockFile extends LockFileTestCase {
    @Override
    protected String[] commandNameWithArgument() {
      return new String[] { TreeCommand.NAME, "result" };
    }
  }

  @Nested
  class FunctionArgs extends FunctionsArgTestCase {
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
      whenSmooth("tree", option, "result");
    }
  }
}
