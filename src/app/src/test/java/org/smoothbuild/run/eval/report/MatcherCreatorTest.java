package org.smoothbuild.run.eval.report;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.Strings.unlines;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.run.eval.report.TaskMatchers.ALL;
import static org.smoothbuild.run.eval.report.TaskMatchers.CALL;
import static org.smoothbuild.run.eval.report.TaskMatchers.COMBINE;
import static org.smoothbuild.run.eval.report.TaskMatchers.CONST;
import static org.smoothbuild.run.eval.report.TaskMatchers.ERROR;
import static org.smoothbuild.run.eval.report.TaskMatchers.FATAL;
import static org.smoothbuild.run.eval.report.TaskMatchers.INFO;
import static org.smoothbuild.run.eval.report.TaskMatchers.NONE;
import static org.smoothbuild.run.eval.report.TaskMatchers.ORDER;
import static org.smoothbuild.run.eval.report.TaskMatchers.PICK;
import static org.smoothbuild.run.eval.report.TaskMatchers.SELECT;
import static org.smoothbuild.run.eval.report.TaskMatchers.WARNING;
import static org.smoothbuild.run.eval.report.TaskMatchers.and;
import static org.smoothbuild.run.eval.report.TaskMatchers.or;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.filesystem.space.Space;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.evaluate.task.Task;
import picocli.CommandLine.TypeConversionException;

public class MatcherCreatorTest extends TestContext {
  @ParameterizedTest
  @MethodSource("provideArguments")
  public void matcher(String expression, TaskMatcher expectedMatcher) throws BytecodeException {
    TaskMatcher matcher = MatcherCreator.createMatcher(expression);

    StringBuilder builder = new StringBuilder();
    var tasks =
        list(combineTask(), constTask(), invokeTask(), orderTask(), pickTask(), selectTask());
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
        arguments("default", or(CALL, INFO)),
        arguments("d", or(CALL, INFO)),
        arguments("none", NONE),
        arguments("n", NONE),
        arguments("fatal", FATAL),
        arguments("lf", FATAL),
        arguments("error", ERROR),
        arguments("le", ERROR),
        arguments("warning", WARNING),
        arguments("lw", WARNING),
        arguments("info", INFO),
        arguments("li", INFO),
        arguments("call", CALL),
        arguments("c", CALL),
        arguments("tuple", COMBINE),
        arguments("t", COMBINE),
        arguments("const", CONST),
        arguments("o", CONST),
        arguments("array", ORDER),
        arguments("r", ORDER),
        arguments("pick", PICK),
        arguments("p", PICK),
        arguments("select", SELECT),
        arguments("s", SELECT),
        arguments("   array", ORDER),
        arguments("array   ", ORDER),
        arguments("   array   ", ORDER),
        arguments("call & error", and(CALL, ERROR)),
        arguments("call | error", or(CALL, ERROR)),
        arguments("call | select | warning", or(CALL, or(SELECT, WARNING))),
        arguments("call & error | select", or(and(CALL, ERROR), SELECT)),
        arguments("select | call & warning", or(and(CALL, WARNING), SELECT)),
        arguments("(call)", CALL),
        arguments("call & (select | warning)", and(CALL, or(SELECT, WARNING))),
        arguments("(select | warning) & call", and(CALL, or(SELECT, WARNING))));
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
          .throwsException(
              new TypeConversionException(unlines("missing ')' at '<EOF>'", "(user", "     ^")));
    }

    @Test
    public void additional_closing_bracket() {
      assertCall(() -> MatcherCreator.createMatcher("(user))"))
          .throwsException(new TypeConversionException(
              unlines("extraneous input ')' expecting <EOF>", "(user))", "      ^")));
    }

    @Test
    public void missing_operator() {
      assertCall(() -> MatcherCreator.createMatcher("user warning"))
          .throwsException(new TypeConversionException(unlines(
              "extraneous input 'warning' expecting <EOF>", "user warning", "     ^^^^^^^")));
    }
  }
}
