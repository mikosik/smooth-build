package org.smoothbuild.systemtest.cli.command.common;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestContext;

public abstract class AbstractValuesArgTestSuite extends SystemTestContext {
  @Test
  void missing_value_arg_causes_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName()));
    assertFinishedWithError();
    assertSystemErrContains("Missing required parameter: '<value>'");
  }

  @Test
  void nonexistent_value_arg_prints_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "unknownValue"));
    assertFinishedWithError();
    assertSystemOutContains(
        """
            :evaluator:findValues
              [ERROR] Unknown value `unknownValue`.
              Try 'smooth list' to see all available values that can be calculated.\
            """);
  }

  @Test
  void value_specified_twice_is_ok() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "result", "result"));
    assertFinishedWithSuccess();
  }

  @Test
  void illegal_value_name_causes_error() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "illegal-name"));
    assertFinishedWithError();
    assertSystemOutContains("[ERROR] Unknown value `illegal-name`.\n");
  }

  @Test
  void illegal_value_names_causes_error_for_each_one() throws Exception {
    createUserModule("""
            result = "abc";
            """);
    runSmooth(new CommandWithArgs(commandName(), "illegal-name", "other-name"));
    assertFinishedWithError();
    assertSystemOutContains("[ERROR] Unknown value `illegal-name`.\n");
    assertSystemOutContains("[ERROR] Unknown value `other-name`.\n");
  }

  @Test
  void func_that_requires_args_prints_error() throws Exception {
    createUserModule(
        """
            String testStringIdentity(String value) = value;
            """);
    runSmooth(new CommandWithArgs(commandName(), "testStringIdentity"));
    assertFinishedWithError();
    assertSystemOutContains(
        """
        :evaluator:findValues
          [ERROR] `testStringIdentity` cannot be calculated as it is not a value but a function.
        """);
  }

  @Test
  void func_which_all_params_are_optional_prints_error() throws Exception {
    createUserModule(
        """
            String testStringIdentity(String value = "default") = value;
            """);
    runSmooth(new CommandWithArgs(commandName(), "testStringIdentity"));
    assertFinishedWithError();
    assertSystemOutContains(
        """
        :evaluator:findValues
          [ERROR] `testStringIdentity` cannot be calculated as it is not a value but a function.
        """);
  }

  protected abstract String commandName();
}
