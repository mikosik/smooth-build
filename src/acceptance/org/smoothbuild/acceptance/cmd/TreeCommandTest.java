package org.smoothbuild.acceptance.cmd;

import static org.smoothbuild.util.Strings.unlines;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.cli.TreeCommand;

@SuppressWarnings("ClassCanBeStatic")
public class TreeCommandTest extends AcceptanceTestCase {
  @Test
  public void with_parameter_and_array() throws Exception {
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
  public void with_long_string_literal() throws Exception {
    givenScript(
        "  result = '012345678901234567890123456789';  ");
    whenSmoothTree("result");
    thenFinishedWithSuccess();
    thenSysOutContains(quotesX2(unlines(
        "String result",
        "  String '012345678901234'...",
        "")));
  }

  @Test
  public void with_convert_computation() throws Exception {
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

  @Test
  public void fails_when_script_file_is_missing() {
    whenSmoothTree("result");
    thenFinishedWithError();
    thenSysOutContains("error: 'build.smooth' doesn't exist.\n");
  }

  @Nested
  class FunctionArgs extends FunctionsArgTestCase {
    @Override
    protected String commandName() {
      return TreeCommand.NAME;
    }
  }
}
