package org.smoothbuild.cli.work.build;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.smoothbuild.SmoothConstants.DEFAULT_SCRIPT;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.cli.work.build.err.DuplicatedFunctionNameWarning;
import org.smoothbuild.cli.work.build.err.IllegalFunctionNameError;
import org.smoothbuild.testing.message.FakeLoggedMessages;

public class CommandLineParserTest {
  String functionName = "function1";
  FakeLoggedMessages messages = new FakeLoggedMessages();
  CommandLineParser parser = new CommandLineParser(messages);
  CommandLineArguments args;

  @Test
  public void functions_to_run() {
    given(args = parser.parse(asList(functionName)));
    when(args.functionsToRun());
    thenReturned(contains(name(functionName)));
  }

  @Test
  public void illegal_function_name_is_logged_as_error() throws Exception {
    parser.parse(asList("illegal^namme"));
    messages.assertContainsOnly(IllegalFunctionNameError.class);
  }

  @Test
  public void dpulicate_is_detected() throws Exception {
    parser.parse(asList("abc", "def", "abc"));
    messages.assertContainsOnly(DuplicatedFunctionNameWarning.class);
  }

  @Test
  public void scriptFile() throws Exception {
    CommandLineArguments args = parser.parse(asList(functionName));
    assertEquals(DEFAULT_SCRIPT, args.scriptFile());
  }
}
