package org.smoothbuild.app.run.eval.report;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.and;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.or;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Log.warning;
import static org.smoothbuild.evaluator.EvaluateConstants.EVALUATE;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.ReportMatcher;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class ReportMatchersTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("matcher_matches_cases")
  public void matcher_matches(
      ReportMatcher matcher, Label label, List<Log> logs, boolean expected) {
    assertThat(matcher.matches(label, logs)).isEqualTo(expected);
  }

  public static List<Arguments> matcher_matches_cases() {
    return list(
        arguments(matcher("all"), null, null, true),
        arguments(matcher("none"), null, null, false),
        arguments(matcher("fatal"), null, list(fatal("fatal")), true),
        arguments(matcher("fatal"), null, list(error("error")), false),
        arguments(matcher("fatal"), null, list(warning("warning")), false),
        arguments(matcher("fatal"), null, list(info("info")), false),
        arguments(matcher("error"), null, list(fatal("fatal")), true),
        arguments(matcher("error"), null, list(error("error")), true),
        arguments(matcher("error"), null, list(warning("warning")), false),
        arguments(matcher("error"), null, list(info("info")), false),
        arguments(matcher("warning"), null, list(fatal("fatal")), true),
        arguments(matcher("warning"), null, list(error("error")), true),
        arguments(matcher("warning"), null, list(warning("warning")), true),
        arguments(matcher("warning"), null, list(info("info")), false),
        arguments(matcher("info"), null, list(fatal("fatal")), true),
        arguments(matcher("info"), null, list(error("error")), true),
        arguments(matcher("info"), null, list(warning("warning")), true),
        arguments(matcher("info"), null, list(info("info")), true),
        arguments(and(matcher("all"), matcher("all")), null, null, true),
        arguments(and(matcher("all"), matcher("none")), null, null, false),
        arguments(and(matcher("none"), matcher("all")), null, null, false),
        arguments(and(matcher("none"), matcher("none")), null, null, false),
        arguments(or(matcher("all"), matcher("all")), null, null, true),
        arguments(or(matcher("all"), matcher("none")), null, null, true),
        arguments(or(matcher("none"), matcher("all")), null, null, true),
        arguments(or(matcher("none"), matcher("none")), null, null, false),
        arguments(matcher("invoke"), EVALUATE.append(label("invoke")), null, true),
        arguments(matcher("invoke"), EVALUATE.append(label("order")), null, false),
        arguments(matcher("combine"), EVALUATE.append(label("combine")), null, true),
        arguments(matcher("combine"), EVALUATE.append(label("order")), null, false),
        arguments(matcher("const"), EVALUATE.append(label("const")), null, true),
        arguments(matcher("const"), EVALUATE.append(label("order")), null, false),
        arguments(matcher("order"), EVALUATE.append(label("order")), null, true),
        arguments(matcher("order"), EVALUATE.append(label("invoke")), null, false),
        arguments(matcher("pick"), EVALUATE.append(label("pick")), null, true),
        arguments(matcher("pick"), EVALUATE.append(label("order")), null, false),
        arguments(matcher("select"), EVALUATE.append(label("select")), null, true),
        arguments(matcher("select"), EVALUATE.append(label("order")), null, false));
  }

  private static ReportMatcher matcher(String name) {
    return switch (name) {
      case "true" -> (task, logs) -> true;
      case "false" -> (task, logs) -> false;
      default -> ReportMatchers.findMatcher(name).get();
    };
  }
}
