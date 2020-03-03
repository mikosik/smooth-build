package org.smoothbuild.acceptance.cmd;

import static org.smoothbuild.util.Strings.unlines;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class TreeCommandTest extends AcceptanceTestCase {

  @Test
  public void tree() throws Exception {
    givenScript(
        "  mySingleton(String element) = [element, 'def'];  ",
        "  result = mySingleton('abc');                     ");
    whenSmoothTree("result");
    thenFinishedWithSuccess();
    thenOutputContains(quotesX2(unlines(
        "[String] result",
        "  [String] mySingleton",
        "    [String] [String]",
        "      String 'abc'",
        "      String 'def'")));
  }

  @Test
  public void tree_with_long_string_literal() throws Exception {
    givenScript(
        "  result = '012345678901234567890123456789';  ");
    whenSmoothTree("result");
    thenFinishedWithSuccess();
    thenOutputContains(quotesX2(unlines(
        "String result",
        "  String '012345678901234'...",
        "")));
  }

  @Test
  public void tree_with_convert_computation() throws Exception {
    givenScript(
        "  Blob result = file(toBlob('abc'), 'name.txt');  ");
    whenSmoothTree("result");
    thenFinishedWithSuccess();
    thenOutputContains(quotesX2(unlines(
        "Blob result",
        "  Blob ~conversion",
        "    File file",
        "      Blob toBlob",
        "        String 'abc'",
        "      String 'name.txt'")));
  }

  @Test
  public void tree_command_fails_when_script_file_is_missing() {
    whenSmoothTree("result");
    thenFinishedWithError();
    thenOutputContains("error: Cannot read build script file 'build.smooth'.\n");
  }

  @Test
  public void tree_command_without_function_argument_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree();
    thenFinishedWithError();
    thenOutputContains(
        "error: Specify at least one function to be executed.",
        "Use 'smooth list' to see all available functions.",
        "");
  }

  @Test
  public void tree_command_with_function_that_requires_arguments_prints_error() throws Exception {
    givenScript(
        "  withArguments(String element) = element;  ");
    whenSmoothTree("withArguments");
    thenFinishedWithError();
    thenOutputContains("error: Cannot print execution tree for 'withArguments' function as " +
        "it requires arguments.");
  }

  @Test
  public void tree_command_with_function_which_all_params_are_optional_is_allowed() throws Exception {
    givenScript(
        "  String testStringIdentity(String value = 'default') = value;  ");
    whenSmoothTree("testStringIdentity");
    thenFinishedWithSuccess();
  }

  @Test
  public void tree_command_with_nonexistent_function_argument_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("nonexistentFunction");
    thenFinishedWithError();
    thenOutputContains(
        "error: Unknown function 'nonexistentFunction'.",
        "Use 'smooth list' to see all available functions.",
        "");
  }

  @Test
  public void tree_command_with_illegal_function_name_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("illegal^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
  }

  @Test
  public void tree_command_with_illegal_function_names_prints_error_for_each_one()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("illegal^name other^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
    thenOutputContains("error: Illegal function name 'other^name' passed in command line.\n");
  }

  @Test
  public void tree_command_with_function_specified_twice_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("result", "result");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
  }

  @Test
  public void tree_command_with_many_functions_specified_twice_prints_error_for_each_one()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("result", "result", "other", "other");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
    thenOutputContains("error: Function 'other' has been specified more than once.\n");
  }
}
