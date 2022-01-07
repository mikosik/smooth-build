package org.smoothbuild.cli.taskmatcher;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.cli.taskmatcher.MatcherCreator.createMatcher;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.ALL;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.AT_LEAST_ERROR;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.AT_LEAST_FATAL;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.AT_LEAST_INFO;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.AT_LEAST_WARNING;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.CALL;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.COMBINE;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.CONST;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.CONVERT;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.INVOKE;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.NONE;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.ORDER;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.PICK;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.PRJ;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.SDK;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.SELECT;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.and;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.or;
import static org.smoothbuild.io.fs.base.Path.path;
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
import org.smoothbuild.cli.console.Level;
import org.smoothbuild.cli.console.Log;
import org.smoothbuild.io.fs.space.Space;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.vm.job.job.JobInfo;
import org.smoothbuild.vm.job.job.JobKind;

import picocli.CommandLine.TypeConversionException;

public class MatcherCreatorTest {
  @ParameterizedTest
  @MethodSource("provideArguments")
  public void matcher(String expression, TaskMatcher expectedMatcher) {
    TaskMatcher matcher = createMatcher(expression);

    StringBuilder builder = new StringBuilder();
    for (JobKind kind : JobKind.values()) {
      for (Space space : Space.values()) {
        for (Level level : levels()) {
          JobInfo jobInfo = taskInfo(kind, space);
          List<Log> logs = level == null ? list() : list(new Log(level, "message"));
          boolean actual = matcher.matches(jobInfo, logs);
          boolean expected = expectedMatcher.matches(jobInfo, logs);
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

  private static JobInfo taskInfo(JobKind kind, Space space) {
    Loc loc = new Loc(filePath(space, path("path")), 3);
    return new JobInfo(kind, "name", loc);
  }

  public static Stream<? extends Arguments> provideArguments() {
    return Stream.of(
        arguments("all", ALL),
        arguments("a", ALL),
        arguments("default", or(and(PRJ, CALL), AT_LEAST_INFO)),
        arguments("d", or(and(PRJ, CALL), AT_LEAST_INFO)),
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

        arguments("project", PRJ),
        arguments("prj", PRJ),
        arguments("sdk", SDK),

        arguments("call", CALL),
        arguments("c", CALL),
        arguments("combine", COMBINE),
        arguments("b", COMBINE),
        arguments("const", CONST),
        arguments("t", CONST),
        arguments("convert", CONVERT),
        arguments("r", CONVERT),
        arguments("invoke", INVOKE),
        arguments("i", INVOKE),
        arguments("order", ORDER),
        arguments("o", ORDER),
        arguments("pick", PICK),
        arguments("p", PICK),
        arguments("select", SELECT),
        arguments("s", SELECT),

        arguments("   project", PRJ),
        arguments("project   ", PRJ),
        arguments("   project   ", PRJ),

        arguments("call & project", and(CALL, PRJ)),
        arguments("call & project & warning", and(CALL, and(PRJ, AT_LEAST_WARNING))),
        arguments("call | project", or(CALL, PRJ)),
        arguments("call | project | warning", or(CALL, or(PRJ, AT_LEAST_WARNING))),
        arguments("call & project | warning", or(and(CALL, PRJ), AT_LEAST_WARNING)),
        arguments("warning | call & project", or(and(CALL, PRJ), AT_LEAST_WARNING)),
        arguments("(call)", CALL),
        arguments("call & (project | warning)", and(CALL, or(PRJ, AT_LEAST_WARNING))),
        arguments("(project | warning) & call", and(CALL, or(PRJ, AT_LEAST_WARNING)))
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
