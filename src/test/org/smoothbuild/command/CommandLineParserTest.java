package org.smoothbuild.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.function.base.Name.simpleName;

import org.junit.Test;
import org.smoothbuild.command.err.CommandLineError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.message.message.ErrorMessageException;

public class CommandLineParserTest {
  String functionName = "function1";
  CommandLineParser parser = new CommandLineParser();

  @Test
  public void functionToRun() {
    CommandLineArguments args = parser.parse(functionName);
    assertThat(args.functionToRun()).isEqualTo(simpleName(functionName));
  }

  public void functionToRunMustBePresent() throws Exception {
    try {
      parser.parse();
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(NothingToDoError.class);
    }
  }

  public void atMostOneFunctionToRunCanBePresent() throws Exception {
    try {
      parser.parse(functionName, functionName);
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(CommandLineError.class);
    }
  }

  @Test
  public void scriptFile() throws Exception {
    CommandLineArguments args = parser.parse(functionName);
    assertThat(args.scriptFile()).isEqualTo(DEFAULT_SCRIPT);
  }

}
