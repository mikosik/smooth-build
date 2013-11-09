package org.smoothbuild.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.function.base.Name.name;

import org.junit.Test;
import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.message.ErrorMessageMatchers;

import com.google.common.collect.ImmutableList;

public class CommandLineParserTest {
  String functionName = "function1";
  CommandLineParser parser = new CommandLineParser();

  @Test
  public void functionToRun() {
    CommandLineArguments args = parser.parse(ImmutableList.of(functionName));
    assertThat(args.functionsToRun()).isEqualTo(ImmutableList.of(name(functionName)));
  }

  public void functionToRunMustBePresent() throws Exception {
    try {
      parser.parse(ImmutableList.<String> of());
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(NothingToDoError.class);
    }
  }

  @Test
  public void illegal_function_name_is_reported() throws Exception {
    try {
      parser.parse(ImmutableList.of("illegal-namme"));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      ErrorMessageMatchers.containsInstanceOf(IllegalFunctionNameError.class);
    }
  }

  @Test
  public void scriptFile() throws Exception {
    CommandLineArguments args = parser.parse(ImmutableList.of(functionName));
    assertThat(args.scriptFile()).isEqualTo(DEFAULT_SCRIPT);
  }

}
