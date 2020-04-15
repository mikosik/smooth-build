package org.smoothbuild.acceptance.cmd;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public abstract class FunctionsArgTestCase extends AcceptanceTestCase {
  @Test
  public void missing_function_argument_causes_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmooth(commandName());
    thenFinishedWithError();
    thenSysErrContains(
        "Missing required parameter: <function>",
        "",
        "Usage:",
        "smooth " + commandName() + " <function>...",
        "Try 'smooth help " + commandName() + "' for more information.",
        "");
  }

  @Test
  public void nonexistent_function_argument_causes_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmooth(commandName(), "nonexistentFunction");
    thenFinishedWithError();
    thenSysOutContains(
        "error: Unknown function 'nonexistentFunction'.",
        "Try 'smooth list' to see all available functions.\n");
  }

  @Test
  public void function_specified_twice_causes_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmooth(commandName(), "result", "result");
    thenFinishedWithError();
    thenSysOutContains("error: Function 'result' has been specified more than once.\n");
  }

  @Test
  public void many_functions_specified_twice_causes_error_for_each_one()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmooth(commandName(), "result", "result", "other", "other");
    thenFinishedWithError();
    thenSysOutContains("error: Function 'result' has been specified more than once.\n");
    thenSysOutContains("error: Function 'other' has been specified more than once.\n");
  }

  @Test
  public void illegal_function_name_causes_error() throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmooth(commandName(), "illegal^name");
    thenFinishedWithError();
    thenSysOutContains("error: Illegal function name 'illegal^name' passed in command line.\n");
  }

  @Test
  public void illegal_function_names_causes_error_for_each_one()
      throws Exception {
    givenScript(
        "  result = 'abc';  ");
    whenSmooth(commandName(), "illegal^name other^name");
    thenFinishedWithError();
    thenSysOutContains("error: Illegal function name 'illegal^name' passed in command line.\n");
    thenSysOutContains("error: Illegal function name 'other^name' passed in command line.\n");
  }

  @Test
  public void build_command_with_function_that_requires_arguments_prints_error() throws Exception {
    givenScript(
        "  String testStringIdentity(String value) = value;  ");
    whenSmooth(commandName(), "testStringIdentity");
    thenFinishedWithError();
    thenSysOutContains("error: Function 'testStringIdentity' cannot be invoked from command line "
        + "as it requires arguments.\n");
  }

  @Test
  public void build_command_with_function_which_all_params_are_optional_is_allowed()
      throws Exception {
    givenScript(
        "  String testStringIdentity(String value = 'default') = value;  ");
    whenSmooth(commandName(), "testStringIdentity");
    thenFinishedWithSuccess();
  }

  protected abstract String commandName();
}
