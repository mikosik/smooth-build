package org.smoothbuild.cli.taskmatcher;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.cli.taskmatcher.MatcherCreator.createMatcher;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.ALL;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.AT_LEAST_ERROR;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.AT_LEAST_FATAL;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.AT_LEAST_INFO;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.AT_LEAST_WARNING;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.CALL;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.CONVERSION;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.LITERAL;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.NONE;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.SLIB;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.USER;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.and;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.or;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Strings.unlines;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.cli.console.Level;
import org.smoothbuild.cli.console.Log;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.compute.TaskKind;
import org.smoothbuild.lang.base.Space;

import picocli.CommandLine.TypeConversionException;

@SuppressWarnings("ClassCanBeStatic")
public class MatcherCreatorTest {
  @ParameterizedTest
  @MethodSource("provideArguments")
  public void matcher(String expression, TaskMatcher expectedMatcher) {
    TaskMatcher matcher = createMatcher(expression);

    StringBuilder builder = new StringBuilder();
    for (TaskKind kind : TaskKind.values()) {
      for (Space space : Space.values()) {
        for (Level level : Level.values()) {
          Task task = task(kind, space);
          List<Log> logs = List.of(new Log(level, "ignored"));
          boolean actual = matcher.matches(task, logs);
          boolean expected = expectedMatcher.matches(task, logs);
          if (actual != expected) {
            builder
                .append(kind)
                .append(" ")
                .append(space)
                .append(" ")
                .append(level)
                .append(" expected=")
                .append(expected)
                .append(" actual=")
                .append(actual)
                .append("\n");
          }
        }
      }
    }
    String failures = builder.toString();
    if (!failures.isEmpty()) {
      fail("Matcher built from parsed expression '" + expression + "' doesn't work as expected:\n"
              + failures);
    }
  }

  private Task task(TaskKind kind, Space space) {
    Task task = mock(Task.class);
    when(task.kind()).thenReturn(kind);
    when(task.space()).thenReturn(space);
    return task;
  }

  public static Stream<? extends Arguments> provideArguments() {
    return Stream.of(
        arguments("all", ALL),
        arguments("default", or(and(USER, CALL), AT_LEAST_INFO)),
        arguments("none", NONE),

        arguments("fatal", AT_LEAST_FATAL),
        arguments("f", AT_LEAST_FATAL),
        arguments("error", AT_LEAST_ERROR),
        arguments("e", AT_LEAST_ERROR),
        arguments("warning", AT_LEAST_WARNING),
        arguments("w", AT_LEAST_WARNING),
        arguments("info", AT_LEAST_INFO),
        arguments("i", AT_LEAST_INFO),

        arguments("user", USER),
        arguments("u", USER),
        arguments("slib", SLIB),
        arguments("s", SLIB),

        arguments("call", CALL),
        arguments("c", CALL),
        arguments("conversion", CONVERSION),
        arguments("conv", CONVERSION),
        arguments("literal", LITERAL),
        arguments("l", LITERAL),

        arguments("   user", USER),
        arguments("user   ", USER),
        arguments("   user   ", USER),

        arguments("call & user", and(CALL, USER)),
        arguments("call & user & warning", and(CALL, and(USER, AT_LEAST_WARNING))),
        arguments("call | user", or(CALL, USER)),
        arguments("call | user | warning", or(CALL, or(USER, AT_LEAST_WARNING))),
        arguments("call & user | warning", or(and(CALL, USER), AT_LEAST_WARNING)),
        arguments("warning | call & user", or(and(CALL, USER), AT_LEAST_WARNING)),
        arguments("(call)", CALL),
        arguments("call & (user | warning)", and(CALL, or(USER, AT_LEAST_WARNING))),
        arguments("(user | warning) & call", and(CALL, or(USER, AT_LEAST_WARNING)))
    );
  }

  @Nested
  class create_matcher_fails_for {
    @Test
    public void empty_string() {
      assertCall(() -> createMatcher(""))
          .throwsException(TypeConversionException.class);
    }

    @Test
    public void missing_closing_bracket() {
      assertCall(() -> createMatcher("(user"))
          .throwsException(new TypeConversionException(unlines(
              "missing ')' at '<EOF>'",
              "(user",
              "     ^"
          )));
    }

    @Test
    public void additional_closing_bracket() {
      assertCall(() -> createMatcher("(user))"))
          .throwsException(new TypeConversionException(unlines(
              "extraneous input ')' expecting <EOF>",
              "(user))",
              "      ^"
          )));
    }

    @Test
    public void missing_operator() {
      assertCall(() -> createMatcher("user warning"))
          .throwsException(new TypeConversionException(unlines(
              "extraneous input 'warning' expecting <EOF>",
              "user warning",
              "     ^^^^^^^"
          )));
    }
  }
}
