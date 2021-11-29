package org.smoothbuild.acceptance.cli.command.common;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;

public abstract class ValuesArgTestCase extends AcceptanceTestCase {
  @Test
  public void missing_value_arg_causes_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName()));
    assertFinishedWithError();
    assertSysErrContains(
        "Missing required parameter: '<value>'",
        "");
  }

  @Test
  public void nonexistent_value_arg_causes_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "unknownValue"));
    assertFinishedWithError();
    assertSysOutContains(
        sectionName(),
        "  Validating arguments",
        "   + ERROR: Unknown value `unknownValue`.",
        "     Try 'smooth list' to see all available values that can be calculated.",
        "");
  }

  @Test
  public void value_specified_twice_causes_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "result", "result"));
    assertFinishedWithError();
    assertSysOutContains("error: Value `result` has been specified more than once.\n");
  }

  @Test
  public void many_values_specified_twice_causes_error_for_each_one() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "result", "result", "other", "other"));
    assertFinishedWithError();
    assertSysOutContains("error: Value `result` has been specified more than once.\n");
    assertSysOutContains("error: Value `other` has been specified more than once.\n");
  }

  @Test
  public void illegal_value_name_causes_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "illegal^name"));
    assertFinishedWithError();
    assertSysOutContains("error: Illegal value name `illegal^name` passed in command line.\n");
  }

  @Test
  public void illegal_value_names_causes_error_for_each_one() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "illegal^name", "other^name"));
    assertFinishedWithError();
    assertSysOutContains("error: Illegal value name `illegal^name` passed in command line.\n");
    assertSysOutContains("error: Illegal value name `other^name` passed in command line.\n");
  }

  @Test
  public void func_that_requires_args_prints_error() throws Exception {
    createUserModule("""
            String testStringIdentity(String value) = value;
            """);
    runSmooth(new CommandWithArgs(commandName(), "testStringIdentity"));
    assertFinishedWithError();
    assertSysOutContains(
        sectionName(),
        "  Validating arguments",
        "   + ERROR: `testStringIdentity` cannot be calculated as it is not a value but a function.",
        "Summary",
        "  1 error",
        "");
  }

  @Test
  public void func_which_all_params_are_optional_prints_error() throws Exception {
    createUserModule("""
            String testStringIdentity(String value = "default") = value;
            """);
    runSmooth(new CommandWithArgs(commandName(), "testStringIdentity"));
    assertFinishedWithError();
    assertSysOutContains(
        sectionName(),
        "  Validating arguments",
        "   + ERROR: `testStringIdentity` cannot be calculated as it is not a value but a function.",
        "Summary",
        "  1 error",
        "");
  }

  protected abstract String commandName();

  protected abstract String sectionName();
}
