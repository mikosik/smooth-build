package org.smoothbuild.cli.match;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.cli.match.ReportMatchers.and;
import static org.smoothbuild.cli.match.ReportMatchers.findMatcher;
import static org.smoothbuild.cli.match.ReportMatchers.labelMatcher;
import static org.smoothbuild.cli.match.ReportMatchers.or;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Log.warning;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.virtualmachine.VmConstants.VM_EVALUATE;

import java.util.function.Predicate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;

public class ReportMatchersTest {
  @ParameterizedTest
  @MethodSource("matcher_matches_cases")
  public void matcher_matches(
      Predicate<Report> matcher, Label label, List<Log> logs, boolean expected) {
    assertThat(matcher.test(report(label, logs))).isEqualTo(expected);
  }

  public static List<Arguments> matcher_matches_cases() {
    return list(
        arguments(findMatcher("all"), null, null, true),
        arguments(findMatcher("none"), null, null, false),
        arguments(findMatcher("fatal"), null, list(fatal("fatal")), true),
        arguments(findMatcher("fatal"), null, list(error("error")), false),
        arguments(findMatcher("fatal"), null, list(warning("warning")), false),
        arguments(findMatcher("fatal"), null, list(info("info")), false),
        arguments(findMatcher("error"), null, list(fatal("fatal")), true),
        arguments(findMatcher("error"), null, list(error("error")), true),
        arguments(findMatcher("error"), null, list(warning("warning")), false),
        arguments(findMatcher("error"), null, list(info("info")), false),
        arguments(findMatcher("warning"), null, list(fatal("fatal")), true),
        arguments(findMatcher("warning"), null, list(error("error")), true),
        arguments(findMatcher("warning"), null, list(warning("warning")), true),
        arguments(findMatcher("warning"), null, list(info("info")), false),
        arguments(findMatcher("info"), null, list(fatal("fatal")), true),
        arguments(findMatcher("info"), null, list(error("error")), true),
        arguments(findMatcher("info"), null, list(warning("warning")), true),
        arguments(findMatcher("info"), null, list(info("info")), true),
        arguments(and(findMatcher("all"), findMatcher("all")), null, null, true),
        arguments(and(findMatcher("all"), findMatcher("none")), null, null, false),
        arguments(and(findMatcher("none"), findMatcher("all")), null, null, false),
        arguments(and(findMatcher("none"), findMatcher("none")), null, null, false),
        arguments(or(findMatcher("all"), findMatcher("all")), null, null, true),
        arguments(or(findMatcher("all"), findMatcher("none")), null, null, true),
        arguments(or(findMatcher("none"), findMatcher("all")), null, null, true),
        arguments(or(findMatcher("none"), findMatcher("none")), null, null, false),
        arguments(labelMatcher(":vm:evaluate:invoke"), VM_EVALUATE.append(":invoke"), null, true),
        arguments(labelMatcher(":vm:*:invoke"), VM_EVALUATE.append(":invoke"), null, true),
        arguments(labelMatcher("**:invoke"), VM_EVALUATE.append(":invoke"), null, true),
        arguments(labelMatcher("**:invoke"), VM_EVALUATE.append(":order"), null, false),
        arguments(labelMatcher("**:combine"), VM_EVALUATE.append(":combine"), null, true),
        arguments(labelMatcher("**:combine"), VM_EVALUATE.append(":order"), null, false));
  }
}
