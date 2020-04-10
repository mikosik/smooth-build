package org.smoothbuild.acceptance.cmd;

import static org.smoothbuild.util.Strings.unlines;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class TreeCommandTest extends AcceptanceTestCase {

  @Test
  public void with_parameter_and_array() throws Exception {
    givenScript(
        "  mySingleton(String element) = [element, 'def'];  ",
        "  result = mySingleton('abc');                     ");
    whenSmoothTree("result");
    thenFinishedWithSuccess();
    thenOutputContains(quotesX2(unlines(
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
    thenOutputContains(quotesX2(unlines(
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
    thenOutputContains(quotesX2(unlines(
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
    thenOutputContains("error: 'build.smooth' doesn't exist.\n");
  }

  @Test
  public void fails_when_no_function_is_specified() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree();
    thenFinishedWithError();
    thenErrorContains(
        "Missing required parameter: <function>",
        "",
        "Usage:",
        "smooth tree <function>...",
        "Try 'smooth help tree' for more information.",
        "");
  }

  @Test
  public void fails_when_specified_function_requires_argument() throws Exception {
    givenScript(
        "  withArguments(String element) = element;  ");
    whenSmoothTree("withArguments");
    thenFinishedWithError();
    thenOutputContains("error: Cannot print execution tree for 'withArguments' function as " +
        "it requires arguments.");
  }

  @Test
  public void works_when_function_has_all_params_optional() throws Exception {
    givenScript(
        "  String testStringIdentity(String value = 'default') = value;  ");
    whenSmoothTree("testStringIdentity");
    thenFinishedWithSuccess();
  }

  @Test
  public void fails_when_function_doesnt_exist() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("nonexistentFunction");
    thenFinishedWithError();
    thenOutputContains(
        "error: Unknown function 'nonexistentFunction'.",
        "Try 'smooth list' to see all available functions.",
        "");
  }

  @Test
  public void fails_when_function_name_is_illegal() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("illegal^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
  }

  @Test
  public void fails_when_more_function_names_are_illegal()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("illegal^name other^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
    thenOutputContains("error: Illegal function name 'other^name' passed in command line.\n");
  }

  @Test
  public void fails_when_function_is_specified_more_than_once() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("result", "result");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
  }

  @Test
  public void prints_error_for_every_function_that_is_specified_more_than_once()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothTree("result", "result", "other", "other");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
    thenOutputContains("error: Function 'other' has been specified more than once.\n");
  }
}
