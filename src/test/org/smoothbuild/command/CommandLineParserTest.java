package org.smoothbuild.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.function.base.Name.simpleName;

import org.junit.Test;
import org.smoothbuild.command.err.CommandLineError;
import org.smoothbuild.command.err.NothingToDoError;

public class CommandLineParserTest {
  String functionName = "function1";
  CommandLineParser parser = new CommandLineParser();

  @Test
  public void functionToRun() {
    CommandLineArguments args = parser.parse(functionName);
    assertThat(args.functionToRun()).isEqualTo(simpleName(functionName));
  }

  @Test(expected = NothingToDoError.class)
  public void functionToRunMustBePresent() throws Exception {
    parser.parse();
  }

  @Test(expected = CommandLineError.class)
  public void atMostOneFunctionToRunCanBePresent() throws Exception {
    parser.parse(functionName, functionName);
  }

  @Test
  public void scriptFile() throws Exception {
    CommandLineArguments args = parser.parse(functionName);
    assertThat(args.scriptFile()).isEqualTo(DEFAULT_SCRIPT);
  }

}
