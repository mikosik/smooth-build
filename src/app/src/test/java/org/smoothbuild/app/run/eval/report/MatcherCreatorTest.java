package org.smoothbuild.app.run.eval.report;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
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
import static org.smoothbuild.app.run.eval.report.ReportMatchers.labelPrefixMatcher;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.not;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.or;
import static org.smoothbuild.common.base.Strings.unlines;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.evaluator.EvaluateConstants.EVALUATE;

import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.app.layout.SmoothSpace;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.filesystem.base.Space;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;
import picocli.CommandLine.TypeConversionException;

public class MatcherCreatorTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("provideArguments")
  public void matcher_instances_matches_same_reports_as_expected_matcher(
      String expression, ReportMatcher expectedMatcher) {

    var taskLabels = list("combine", "const", "invoke", "order", "pick", "select")
        .map(s -> EVALUATE.append(label(s)))
        .append(label("not-evaluate"));

    var expectedUpdated = or(expectedMatcher, not(labelPrefixMatcher(EVALUATE)));
    verifyCreatedMatcherInstanceMatchesSameReportsAsExpectedMatcher(
        expression, expectedUpdated, taskLabels);
  }

  private static void verifyCreatedMatcherInstanceMatchesSameReportsAsExpectedMatcher(
      String expression, ReportMatcher expectedMatcher, List<Label> taskLabels) {
    StringBuilder builder = new StringBuilder();
    ReportMatcher matcher = MatcherCreator.createMatcher(expression);
    for (Label label : taskLabels) {
      for (Space space : SmoothSpace.values()) {
        for (Level level : Level.values()) {
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
