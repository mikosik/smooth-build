package org.smoothbuild.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Name.simpleName;

import org.junit.Test;
import org.smoothbuild.command.err.CommandLineError;
import org.smoothbuild.command.err.NothingToDoError;
import org.smoothbuild.testing.problem.TestingProblemsListener;

public class CommandLineParserTest {
  String functionName = "function1";
  CommandLineParser parser = new CommandLineParser();
  TestingProblemsListener problems = new TestingProblemsListener();

  @Test
  public void functionToRun() {
    CommandLineArguments args = parser.parse(problems, functionName);

    assertThat(args.functionToRun()).isEqualTo(simpleName(functionName));
    problems.assertNoProblems();
  }

  @Test
  public void functionToRunMustBePresent() throws Exception {
    parser.parse(problems);
    problems.assertOnlyProblem(NothingToDoError.class);
  }

  @Test
  public void atMostOneFunctionToRunCanBePresent() throws Exception {
    parser.parse(problems, functionName, functionName);
    problems.assertOnlyProblem(CommandLineError.class);
  }

  @Test
  public void scriptFile() throws Exception {
    CommandLineArguments args = parser.parse(problems, functionName);

    assertThat(args.scriptFile()).isEqualTo("build.smooth");
    problems.assertNoProblems();
  }

}
