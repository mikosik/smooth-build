package org.smoothbuild.systemtest.cli.command.common;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestCase;

public abstract class AbstractValuesArgTestSuite extends SystemTestCase {
  @Test
  public void missing_value_arg_causes_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName()));
    assertFinishedWithError();
    assertSysErrContains("Missing required parameter: '<value>'");
  }

  @Test
  public void nonexistent_value_arg_causes_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "unknownValue"));
    assertFinishedWithError();
    assertSysOutContains(
        """
              command line arguments
               + ERROR: Unknown value `unknownValue`.
                 Try 'smooth list' to see all available values that can be calculated.\
            """
    );
  }

  @Test
  public void value_specified_twice_is_ok() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "result", "result"));
    assertFinishedWithSuccess();
  }

  @Test
  public void illegal_value_name_causes_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "illegal^name"));
    assertFinishedWithError();
    assertSysOutContains("ERROR: Unknown value `illegal^name`.\n");
  }

  @Test
  public void illegal_value_names_causes_error_for_each_one() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "illegal^name", "other^name"));
    assertFinishedWithError();
    assertSysOutContains("ERROR: Unknown value `illegal^name`.\n");
    assertSysOutContains("ERROR: Unknown value `other^name`.\n");
  }

  @Test
  public void func_that_requires_args_prints_error() throws Exception {
    createUserModule("""
            String testStringIdentity(String value) = value;
            """);
    runSmooth(new CommandWithArgs(commandName(), "testStringIdentity"));
    assertFinishedWithError();
    assertSysOutContains("""
          command line arguments
           + ERROR: `testStringIdentity` cannot be calculated as it is not a value but a function.
        Summary
          1 error
        """);
  }

  @Test
  public void func_which_all_params_are_optional_prints_error() throws Exception {
    createUserModule("""
            String testStringIdentity(String value = "default") = value;
            """);
    runSmooth(new CommandWithArgs(commandName(), "testStringIdentity"));
    assertFinishedWithError();
    assertSysOutContains("""
          command line arguments
           + ERROR: `testStringIdentity` cannot be calculated as it is not a value but a function.
        Summary
          1 error
        """);
  }

  protected abstract String commandName();

  protected abstract String sectionName();
}
