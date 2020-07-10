package org.smoothbuild.acceptance.cli.command.common;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;

public abstract class FunctionsArgTestCase extends AcceptanceTestCase {
  @Test
  public void missing_function_argument_causes_error() throws Exception {
    createUserModule(
        "  result = 'abc';  ");
    runSmooth(new CommandWithArgs(commandName()));
    assertFinishedWithError();
    assertSysErrContains(
        "Missing required parameter: '<function>'",
        "");
  }

  @Test
  public void nonexistent_function_argument_causes_error() throws Exception {
    createUserModule(
        "  result = 'abc';  ");
    runSmooth(new CommandWithArgs(commandName(), "nonexistentFunction"));
    assertFinishedWithError();
    assertSysOutContains(
        sectionName(),
        "  Validating arguments",
        "   + ERROR: Unknown function 'nonexistentFunction'.",
        "     Try 'smooth list' to see all available functions.",
        "");
  }

  @Test
  public void function_specified_twice_causes_error() throws Exception {
    createUserModule(
        "  result = 'abc';  ");
    runSmooth(new CommandWithArgs(commandName(), "result", "result"));
    assertFinishedWithError();
    assertSysOutContains("error: Function 'result' has been specified more than once.\n");
  }

  @Test
  public void many_functions_specified_twice_causes_error_for_each_one()
      throws Exception {
    createUserModule(
        "  result = 'abc';  ");
    runSmooth(new CommandWithArgs(commandName(), "result", "result", "other", "other"));
    assertFinishedWithError();
    assertSysOutContains("error: Function 'result' has been specified more than once.\n");
    assertSysOutContains("error: Function 'other' has been specified more than once.\n");
  }

  @Test
  public void illegal_function_name_causes_error() throws Exception {
    createUserModule(
        "  result = 'abc';  ");
    runSmooth(new CommandWithArgs(commandName(), "illegal^name"));
    assertFinishedWithError();
    assertSysOutContains("error: Illegal function name 'illegal^name' passed in command line.\n");
  }

  @Test
  public void illegal_function_names_causes_error_for_each_one()
      throws Exception {
    createUserModule(
        "  result = 'abc';  ");
    runSmooth(new CommandWithArgs(commandName(), "illegal^name", "other^name"));
    assertFinishedWithError();
    assertSysOutContains("error: Illegal function name 'illegal^name' passed in command line.\n");
    assertSysOutContains("error: Illegal function name 'other^name' passed in command line.\n");
  }

  @Test
  public void build_command_with_function_that_requires_arguments_prints_error() throws Exception {
    createUserModule(
        "  String testStringIdentity(String value) = value;  ");
    runSmooth(new CommandWithArgs(commandName(), "testStringIdentity"));
    assertFinishedWithError();
    assertSysOutContains(
        sectionName(),
        "  Validating arguments",
        "   + ERROR: Function 'testStringIdentity' cannot be invoked from command line as it requires arguments.",
        "Summary",
        "  1 error",
        "");
  }

  @Test
  public void build_command_with_function_which_all_params_are_optional_is_allowed()
      throws Exception {
    createUserModule(
        "  String testStringIdentity(String value = 'default') = value;  ");
    runSmooth(new CommandWithArgs(commandName(), "testStringIdentity"));
    assertFinishedWithSuccess();
  }

  protected abstract String commandName();

  protected abstract String sectionName();
}
