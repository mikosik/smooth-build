package org.smoothbuild.out.report;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.io.fs.base.PathS.path;
import static org.smoothbuild.io.fs.space.FilePath.filePath;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.io.fs.space.Space;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.vm.job.job.TaskInfo;
import org.smoothbuild.vm.job.job.TaskKind;

import picocli.CommandLine.TypeConversionException;

public class MatcherCreatorTest {
  @ParameterizedTest
  @MethodSource("provideArguments")
  public void matcher(String expression, TaskMatcher expectedMatcher) {
    TaskMatcher matcher = MatcherCreator.createMatcher(expression);

    StringBuilder builder = new StringBuilder();
    for (TaskKind kind : TaskKind.values()) {
      for (Space space : Space.values()) {
        for (Level level : levels()) {
          TaskInfo taskInfo = taskInfo(kind, space);
          List<Log> logs = level == null ? list() : list(new Log(level, "message"));
          boolean actual = matcher.matches(taskInfo, logs);
          boolean expected = expectedMatcher.matches(taskInfo, logs);
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

  private static List<Level> levels() {
    ArrayList<Level> levels = new ArrayList<>(asList(Level.values()));
    levels.add(null);
    return levels;
  }

  private static TaskInfo taskInfo(TaskKind kind, Space space) {
    Loc loc = new Loc(filePath(space, path("path")), 3);
    return new TaskInfo(kind, "name", loc);
  }

  public static Stream<? extends Arguments> provideArguments() {
    return Stream.of(
        arguments("all", TaskMatchers.ALL),
        arguments("a", TaskMatchers.ALL),
        arguments("default", TaskMatchers.or(TaskMatchers.and(TaskMatchers.PRJ, TaskMatchers.CALL), TaskMatchers.AT_LEAST_INFO)),
        arguments("d", TaskMatchers.or(TaskMatchers.and(TaskMatchers.PRJ, TaskMatchers.CALL), TaskMatchers.AT_LEAST_INFO)),
        arguments("none", TaskMatchers.NONE),
        arguments("n", TaskMatchers.NONE),

        arguments("fatal", TaskMatchers.AT_LEAST_FATAL),
        arguments("lf", TaskMatchers.AT_LEAST_FATAL),
        arguments("error", TaskMatchers.AT_LEAST_ERROR),
        arguments("le", TaskMatchers.AT_LEAST_ERROR),
        arguments("warning", TaskMatchers.AT_LEAST_WARNING),
        arguments("lw", TaskMatchers.AT_LEAST_WARNING),
        arguments("info", TaskMatchers.AT_LEAST_INFO),
        arguments("li", TaskMatchers.AT_LEAST_INFO),

        arguments("project", TaskMatchers.PRJ),
        arguments("prj", TaskMatchers.PRJ),
        arguments("sdk", TaskMatchers.SDK),

        arguments("call", TaskMatchers.CALL),
        arguments("c", TaskMatchers.CALL),
        arguments("combine", TaskMatchers.COMBINE),
        arguments("b", TaskMatchers.COMBINE),
        arguments("const", TaskMatchers.CONST),
        arguments("t", TaskMatchers.CONST),
        arguments("convert", TaskMatchers.CONVERT),
        arguments("r", TaskMatchers.CONVERT),
        arguments("invoke", TaskMatchers.INVOKE),
        arguments("i", TaskMatchers.INVOKE),
        arguments("order", TaskMatchers.ORDER),
        arguments("o", TaskMatchers.ORDER),
        arguments("select", TaskMatchers.SELECT),
        arguments("s", TaskMatchers.SELECT),

        arguments("   project", TaskMatchers.PRJ),
        arguments("project   ", TaskMatchers.PRJ),
        arguments("   project   ", TaskMatchers.PRJ),

        arguments("call & project", TaskMatchers.and(TaskMatchers.CALL, TaskMatchers.PRJ)),
        arguments("call & project & warning", TaskMatchers.and(
            TaskMatchers.CALL, TaskMatchers.and(TaskMatchers.PRJ, TaskMatchers.AT_LEAST_WARNING))),
        arguments("call | project", TaskMatchers.or(TaskMatchers.CALL, TaskMatchers.PRJ)),
        arguments("call | project | warning", TaskMatchers.or(
            TaskMatchers.CALL, TaskMatchers.or(TaskMatchers.PRJ, TaskMatchers.AT_LEAST_WARNING))),
        arguments("call & project | warning", TaskMatchers.or(
            TaskMatchers.and(TaskMatchers.CALL, TaskMatchers.PRJ), TaskMatchers.AT_LEAST_WARNING)),
        arguments("warning | call & project", TaskMatchers.or(
            TaskMatchers.and(TaskMatchers.CALL, TaskMatchers.PRJ), TaskMatchers.AT_LEAST_WARNING)),
        arguments("(call)", TaskMatchers.CALL),
        arguments("call & (project | warning)", TaskMatchers.and(
            TaskMatchers.CALL, TaskMatchers.or(TaskMatchers.PRJ, TaskMatchers.AT_LEAST_WARNING))),
        arguments("(project | warning) & call", TaskMatchers.and(
            TaskMatchers.CALL, TaskMatchers.or(TaskMatchers.PRJ, TaskMatchers.AT_LEAST_WARNING)))
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
