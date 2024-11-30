package org.smoothbuild.cli.match;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.cli.layout.Aliases.INSTALL_ALIAS;
import static org.smoothbuild.cli.layout.Aliases.LIBRARY_ALIAS;
import static org.smoothbuild.cli.layout.Aliases.PROJECT_ALIAS;
import static org.smoothbuild.cli.match.MatcherCreator.createMatcher;
import static org.smoothbuild.cli.match.ReportMatchers.ALL;
import static org.smoothbuild.cli.match.ReportMatchers.ERROR;
import static org.smoothbuild.cli.match.ReportMatchers.FATAL;
import static org.smoothbuild.cli.match.ReportMatchers.INFO;
import static org.smoothbuild.cli.match.ReportMatchers.NONE;
import static org.smoothbuild.cli.match.ReportMatchers.WARNING;
import static org.smoothbuild.cli.match.ReportMatchers.and;
import static org.smoothbuild.cli.match.ReportMatchers.labelMatcher;
import static org.smoothbuild.cli.match.ReportMatchers.or;
import static org.smoothbuild.common.base.Strings.unlines;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.ReportMatcher;
import picocli.CommandLine.TypeConversionException;

public class MatcherCreatorTest {
  @ParameterizedTest
  @MethodSource("provideArguments")
  public void matcher_instances_matches_same_reports_as_expected_matcher(
      String expression, ReportMatcher expectedMatcher) {

    var taskLabels = list(
        label(":vm:evaluate:combine"),
        label(":vm:evaluate:order"),
        label(":vm:order"),
        label(":vm:evaluate:invoke"),
        label(":vm:evaluate:select"),
        label(":vm:different:order"),
        label(":vm:something"));

    verifyCreatedMatcherInstanceMatchesSameReportsAsExpectedMatcher(
        expression, expectedMatcher, taskLabels);
  }

  private static void verifyCreatedMatcherInstanceMatchesSameReportsAsExpectedMatcher(
      String expression, ReportMatcher expectedMatcher, List<Label> taskLabels) {
    var stringBuilder = new StringBuilder();
    var reportMatcher = createMatcher(expression);
    for (var label : taskLabels) {
      for (var alias : list(PROJECT_ALIAS, LIBRARY_ALIAS, INSTALL_ALIAS)) {
        for (var level : Level.values()) {
          List<Log> logs = level == null ? list() : list(new Log(level, "message"));
          boolean actual = reportMatcher.matches(label, logs);
          boolean expected = expectedMatcher.matches(label, logs);
          if (actual != expected) {
            stringBuilder
                .append(label)
                .append(" ")
                .append(alias)
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
    String failures = stringBuilder.toString();
    if (!failures.isEmpty()) {
      fail("Matcher built from parsed expression '" + expression + "' doesn't work as expected:\n"
          + failures);
    }
  }

  public static Stream<Arguments> provideArguments() {
    return Stream.of(
        arguments("all", ALL),
        arguments("a", ALL),
        arguments("default", or(labelMatcher(":vm:evaluate:invoke"), INFO)),
        arguments("d", or(labelMatcher(":vm:evaluate:invoke"), INFO)),
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
        arguments(":vm:evaluate:invoke", labelMatcher(":vm:evaluate:invoke")),
        arguments(":vm:evaluate:*", labelMatcher(":vm:evaluate:*")),
        arguments(":vm:**", labelMatcher(":vm:**")),
        arguments(":vm:evaluate:combine", labelMatcher(":vm:evaluate:combine")),
        arguments(":vm:evaluate:order", labelMatcher(":vm:evaluate:order")),
        arguments("   :vm:evaluate:order", labelMatcher(":vm:evaluate:order")),
        arguments(":vm:evaluate:order   ", labelMatcher(":vm:evaluate:order")),
        arguments("   :vm:evaluate:order   ", labelMatcher(":vm:evaluate:order")),
        arguments(":vm:evaluate:invoke & error", and(labelMatcher(":vm:evaluate:invoke"), ERROR)),
        arguments(":vm:evaluate:invoke | error", or(labelMatcher(":vm:evaluate:invoke"), ERROR)),
        arguments(
            ":vm:evaluate:invoke | :vm:evaluate:order | warning",
            or(
                labelMatcher(":vm:evaluate:invoke"),
                or(labelMatcher(":vm:evaluate:order"), WARNING))),
        arguments(
            ":vm:evaluate:invoke & error | :vm:evaluate:order",
            or(
                and(labelMatcher(":vm:evaluate:invoke"), ERROR),
                labelMatcher(":vm:evaluate:order"))),
        arguments(
            ":vm:evaluate:order | :vm:evaluate:invoke & warning",
            or(
                and(labelMatcher(":vm:evaluate:invoke"), WARNING),
                labelMatcher(":vm:evaluate:order"))),
        arguments("(:vm:evaluate:invoke)", labelMatcher(":vm:evaluate:invoke")),
        arguments(
            ":vm:evaluate:invoke & (:vm:evaluate:order | warning)",
            and(
                labelMatcher(":vm:evaluate:invoke"),
                or(labelMatcher(":vm:evaluate:order"), WARNING))),
        arguments(
            "(:vm:evaluate:order | warning) & :vm:evaluate:invoke",
            and(
                labelMatcher(":vm:evaluate:invoke"),
                or(labelMatcher(":vm:evaluate:order"), WARNING))));
  }

  @Nested
  class create_matcher_fails_for {
    @Test
    void empty_string() {
      assertCall(() -> createMatcher("")).throwsException(TypeConversionException.class);
    }

    @Test
    void missing_closing_bracket() {
      assertCall(() -> createMatcher("(user"))
          .throwsException(
              new TypeConversionException(unlines("missing ')' at '<EOF>'", "(user", "     ^")));
    }

    @Test
    void additional_closing_bracket() {
      assertCall(() -> createMatcher("(user))"))
          .throwsException(new TypeConversionException(
              unlines("extraneous input ')' expecting <EOF>", "(user))", "      ^")));
    }

    @Test
    void missing_operator() {
      assertCall(() -> createMatcher("user warning"))
          .throwsException(new TypeConversionException(unlines(
              "extraneous input 'warning' expecting <EOF>", "user warning", "     ^^^^^^^")));
    }
  }
}
