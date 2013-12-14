package org.smoothbuild.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.command.SmoothContants.DEFAULT_SCRIPT;
import static org.smoothbuild.lang.function.base.Name.name;

import org.junit.Test;
import org.smoothbuild.command.err.DuplicatedFunctionNameWarning;
import org.smoothbuild.command.err.IllegalFunctionNameError;
import org.smoothbuild.testing.message.FakeMessageGroup;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class CommandLineParserTest {
  String functionName = "function1";
  FakeMessageGroup messages = new FakeMessageGroup();
  CommandLineParser parser = new CommandLineParser(messages);

  @Test
  public void functions_to_run() {
    CommandLineArguments args = parser.parse(ImmutableList.of(functionName));
    assertThat(args.functionsToRun()).isEqualTo(ImmutableSet.of(name(functionName)));
  }

  @Test
  public void illegal_function_name_is_reported() throws Exception {
    parser.parse(ImmutableList.of("illegal^namme"));
    messages.assertContainsOnly(IllegalFunctionNameError.class);
  }

  @Test
  public void dpulicate_is_detected() throws Exception {
    parser.parse(ImmutableList.of("abc", "def", "abc"));
    messages.assertContainsOnly(DuplicatedFunctionNameWarning.class);
  }

  @Test
  public void scriptFile() throws Exception {
    CommandLineArguments args = parser.parse(ImmutableList.of(functionName));
    assertThat(args.scriptFile()).isEqualTo(DEFAULT_SCRIPT);
  }

}
