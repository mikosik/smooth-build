package org.smoothbuild.vm.report;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.report.TaskMatchers.ALL;
import static org.smoothbuild.vm.report.TaskMatchers.AT_LEAST_ERROR;
import static org.smoothbuild.vm.report.TaskMatchers.AT_LEAST_FATAL;
import static org.smoothbuild.vm.report.TaskMatchers.AT_LEAST_INFO;
import static org.smoothbuild.vm.report.TaskMatchers.AT_LEAST_WARNING;
import static org.smoothbuild.vm.report.TaskMatchers.CALL;
import static org.smoothbuild.vm.report.TaskMatchers.COMBINE;
import static org.smoothbuild.vm.report.TaskMatchers.CONST;
import static org.smoothbuild.vm.report.TaskMatchers.NONE;
import static org.smoothbuild.vm.report.TaskMatchers.ORDER;
import static org.smoothbuild.vm.report.TaskMatchers.PICK;
import static org.smoothbuild.vm.report.TaskMatchers.SELECT;
import static org.smoothbuild.vm.report.TaskMatchers.and;
import static org.smoothbuild.vm.report.TaskMatchers.or;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.fs.space.Space;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.task.Task;

import picocli.CommandLine.TypeConversionException;

public class MatcherCreatorTest extends TestContext {
  @ParameterizedTest
  @MethodSource("provideArguments")
  public void matcher(String expression, TaskMatcher expectedMatcher) {
    TaskMatcher matcher = MatcherCreator.createMatcher(expression);

    StringBuilder builder = new StringBuilder();
    var tasks = list(
        combineTask(), constTask(), nativeCallTask(), orderTask(), pickTask(), selectTask());
    for (Task task : tasks) {
      for (Space space : Space.values()) {
        for (Level level : levels()) {
          List<Log> logs = level == null ? list() : list(new Log(level, "message"));
          boolean actual = matcher.matches(task, logs);
          boolean expected = expectedMatcher.matches(task, logs);
          if (actual != expected) {
            builder
                .append(task)
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

  private static List<Level> levels() {
    ArrayList<Level> levels = new ArrayList<>(asList(Level.values()));
    levels.add(null);
    return levels;
  }

  public static Stream<Arguments> provideArguments() {
    return Stream.of(
        arguments("all", ALL),
        arguments("a", ALL),
        arguments("default", or(CALL, AT_LEAST_INFO)),
        arguments("d", or(CALL, AT_LEAST_INFO)),
        arguments("none", NONE),
        arguments("n", NONE),

        arguments("fatal", AT_LEAST_FATAL),
        arguments("lf", AT_LEAST_FATAL),
        arguments("error", AT_LEAST_ERROR),
        arguments("le", AT_LEAST_ERROR),
        arguments("warning", AT_LEAST_WARNING),
        arguments("lw", AT_LEAST_WARNING),
        arguments("info", AT_LEAST_INFO),
        arguments("li", AT_LEAST_INFO),

        arguments("call", CALL),
        arguments("c", CALL),
        arguments("combine", COMBINE),
        arguments("b", COMBINE),
        arguments("const", CONST),
        arguments("t", CONST),
        arguments("order", ORDER),
        arguments("o", ORDER),
        arguments("pick", PICK),
        arguments("p", PICK),
        arguments("select", SELECT),
        arguments("s", SELECT),

        arguments("   order", ORDER),
        arguments("order   ", ORDER),
        arguments("   order   ", ORDER),

        arguments("call & error", and(CALL, AT_LEAST_ERROR)),
        arguments("call | error", or(CALL, AT_LEAST_ERROR)),
        arguments("call | select | warning", or(CALL, or(SELECT, AT_LEAST_WARNING))),
        arguments("call & error | select", or(and(CALL, AT_LEAST_ERROR), SELECT)),
        arguments("select | call & warning", or(and(CALL, AT_LEAST_WARNING), SELECT)),
        arguments("(call)", CALL),
        arguments("call & (select | warning)", and(CALL, or(SELECT, AT_LEAST_WARNING))),
        arguments("(select | warning) & call", and(CALL, or(SELECT, AT_LEAST_WARNING)))
    );
  }

  @Nested
  class create_matcher_fails_for {
    @Test
    public void empty_string() {
      assertCall(() -> MatcherCreator.createMatcher(""))
          .throwsException(TypeConversionException.class);
    }

    @Test
    public void missing_closing_bracket() {
      assertCall(() -> MatcherCreator.createMatcher("(user"))
          .throwsException(new TypeConversionException(unlines(
              "missing ')' at '<EOF>'",
              "(user",
              "     ^"
          )));
    }

    @Test
    public void additional_closing_bracket() {
      assertCall(() -> MatcherCreator.createMatcher("(user))"))
          .throwsException(new TypeConversionException(unlines(
              "extraneous input ')' expecting <EOF>",
              "(user))",
              "      ^"
          )));
    }

    @Test
    public void missing_operator() {
      assertCall(() -> MatcherCreator.createMatcher("user warning"))
          .throwsException(new TypeConversionException(unlines(
              "extraneous input 'warning' expecting <EOF>",
              "user warning",
              "     ^^^^^^^"
          )));
    }
  }
}
