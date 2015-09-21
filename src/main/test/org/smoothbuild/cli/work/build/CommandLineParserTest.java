package org.smoothbuild.cli.work.build;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.SmoothConstants.DEFAULT_SCRIPT;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.testing.message.ContainsOnlyMessageMatcher.containsOnlyMessage;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.cli.work.build.err.DuplicatedFunctionNameWarning;
import org.smoothbuild.cli.work.build.err.IllegalFunctionNameError;
import org.smoothbuild.message.listen.LoggedMessages;

public class CommandLineParserTest {
  private final String functionName = "function1";
  private CommandLineParser parser;
  private CommandLineArguments args;
  private LoggedMessages messages;

  @Test
  public void functions_to_run() {
    given(parser = new CommandLineParser(new LoggedMessages()));
    given(args = parser.parse(asList(functionName)));
    when(args.functionsToRun());
    thenReturned(contains(name(functionName)));
  }

  @Test
  public void illegal_function_name_is_logged_as_error() throws Exception {
    given(messages = new LoggedMessages());
    given(parser = new CommandLineParser(messages));
    when(parser).parse(asList("illegal^namme"));
    then(messages, containsOnlyMessage(IllegalFunctionNameError.class));
  }

  @Test
  public void duplicate_function_name_is_detected() throws Exception {
    given(messages = new LoggedMessages());
    given(parser = new CommandLineParser(messages));
    when(parser).parse(asList("abc", "def", "abc"));
    then(messages, containsOnlyMessage(DuplicatedFunctionNameWarning.class));
  }

  @Test
  public void command_line_arguments_contains_default_script_file() throws Exception {
    given(parser = new CommandLineParser(new LoggedMessages()));
    given(args = parser.parse(asList(functionName)));
    when(args).scriptFile();
    thenReturned(DEFAULT_SCRIPT);
  }
}
