package org.smoothbuild.app.run.eval.report;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.app.run.eval.report.EvaluateConstants.EVALUATE;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.ALL;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.CALL;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.COMBINE;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.CONST;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.ERROR;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.FATAL;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.INFO;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.NONE;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.ORDER;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.PICK;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.SELECT;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.WARNING;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.and;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.or;
import static org.smoothbuild.common.base.Strings.unlines;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.app.layout.SmoothSpace;
import org.smoothbuild.common.filesystem.base.Space;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.ReportMatcher;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;
import picocli.CommandLine.TypeConversionException;

public class MatcherCreatorTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("provideArguments")
  public void matcher(String expression, ReportMatcher expectedMatcher) {
    ReportMatcher matcher = MatcherCreator.createMatcher(expression);

    StringBuilder builder = new StringBuilder();
    var taskLabels = list("combine", "const", "invoke", "order", "pick", "select")
        .map(s -> EVALUATE.append(label(s)));
    for (Label label : taskLabels) {
      for (Space space : SmoothSpace.values()) {
        for (Level level : levels()) {
          List<Log> logs = level == null ? list() : list(new Log(level, "message"));
          boolean actual = matcher.matches(label, logs);
          boolean expected = expectedMatcher.matches(label, logs);
          if (actual != expected) {
            builder
                .append(label)
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
        arguments("combine", COMBINE),
        arguments("const", CONST),
        arguments("order", ORDER),
        arguments("pick", PICK),
        arguments("select", SELECT),
        arguments("   order", ORDER),
        arguments("order   ", ORDER),
        arguments("   order   ", ORDER),
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
