package org.smoothbuild.acceptance.cmd;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class DagCommandTest extends AcceptanceTestCase {

  @Test
  public void dag() throws Exception {
    givenScript(
        "  mySingleton(String element) = [element, 'def'];  ",
        "  result = mySingleton('abc');                     ");
    whenSmoothDag("result");
    thenFinishedWithSuccess();
    thenOutputContains(quotesX2(
        "result([String])\n" +
        "  mySingleton([String])\n" +
        "    [String]([String])\n" +
        "      'abc'(String)\n" +
        "      'def'(String)"));
  }

  @Test
  public void dag_command_fails_when_script_file_is_missing() {
    whenSmoothDag("result");
    thenFinishedWithError();
    thenOutputContains("error: Cannot read build script file 'build.smooth'.\n");
  }

  @Test
  public void dag_command_without_function_argument_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothDag();
    thenFinishedWithError();
    thenOutputContains("error: Specify at least one function to be executed.\n"
        + "Use 'smooth list' to see all available functions.\n");
  }

  @Test
  public void dag_command_with_function_that_requires_arguments_prints_error() throws Exception {
    givenScript(
        "  withArguments(String element) = element;  ");
    whenSmoothDag("withArguments");
    thenFinishedWithError();
    thenOutputContains(
        "error: Cannot print DAG for 'withArguments' function as it requires arguments.");
  }

  @Test
  public void dag_command_with_function_which_all_params_are_optional_is_allowed() throws Exception {
    givenScript(
        "  String testStringIdentity(String value = 'default') = value;  ");
    whenSmoothDag("testStringIdentity");
    thenFinishedWithSuccess();
  }

  @Test
  public void dag_command_with_nonexistent_function_argument_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothDag("nonexistentFunction");
    thenFinishedWithError();
    thenOutputContains("error: Unknown function 'nonexistentFunction'.\n"
        + "Use 'smooth list' to see all available functions.\n");
  }

  @Test
  public void dag_command_with_illegal_function_name_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothDag("illegal^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
  }

  @Test
  public void dag_command_with_illegal_function_names_prints_error_for_each_one()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothDag("illegal^name other^name");
    thenFinishedWithError();
    thenOutputContains("error: Illegal function name 'illegal^name' passed in command line.\n");
    thenOutputContains("error: Illegal function name 'other^name' passed in command line.\n");
  }

  @Test
  public void dag_command_with_function_specified_twice_prints_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothDag("result", "result");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
  }

  @Test
  public void dag_command_with_many_functions_specified_twice_prints_error_for_each_one()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmoothDag("result", "result", "other", "other");
    thenFinishedWithError();
    thenOutputContains("error: Function 'result' has been specified more than once.\n");
    thenOutputContains("error: Function 'other' has been specified more than once.\n");
  }
}
