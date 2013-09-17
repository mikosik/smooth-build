package org.smoothbuild.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.function.base.Name.simpleName;

import org.junit.Test;
import org.smoothbuild.command.err.CommandLineError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.testing.message.TestMessageListener;

public class CommandLineParserTest {
  String functionName = "function1";
  CommandLineParser parser = new CommandLineParser();
  TestMessageListener messages = new TestMessageListener();

  @Test
  public void functionToRun() {
    CommandLineArguments args = parser.parse(messages, functionName);

    assertThat(args.functionToRun()).isEqualTo(simpleName(functionName));
    messages.assertNoProblems();
  }

  @Test
  public void functionToRunMustBePresent() throws Exception {
    parser.parse(messages);
    messages.assertOnlyProblem(NothingToDoError.class);
  }

  @Test
  public void atMostOneFunctionToRunCanBePresent() throws Exception {
    parser.parse(messages, functionName, functionName);
    messages.assertOnlyProblem(CommandLineError.class);
  }

  @Test
  public void scriptFile() throws Exception {
    CommandLineArguments args = parser.parse(messages, functionName);

    assertThat(args.scriptFile()).isEqualTo(DEFAULT_SCRIPT);
    messages.assertNoProblems();
  }

}
