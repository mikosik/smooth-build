package org.smoothbuild.cli.match;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.cli.match.ReportMatchers.and;
import static org.smoothbuild.cli.match.ReportMatchers.or;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.error;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Log.info;
import static org.smoothbuild.common.log.base.Log.warning;
import static org.smoothbuild.virtualmachine.VmConstants.VM_EVALUATE;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.ReportMatcher;

public class ReportMatchersTest {
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
        arguments(matcher("invoke"), VM_EVALUATE.append(":invoke"), null, true),
        arguments(matcher("invoke"), VM_EVALUATE.append(":order"), null, false),
        arguments(matcher("combine"), VM_EVALUATE.append(":combine"), null, true),
        arguments(matcher("combine"), VM_EVALUATE.append(":order"), null, false),
        arguments(matcher("order"), VM_EVALUATE.append(":order"), null, true),
        arguments(matcher("order"), VM_EVALUATE.append(":invoke"), null, false),
        arguments(matcher("pick"), VM_EVALUATE.append(":pick"), null, true),
        arguments(matcher("pick"), VM_EVALUATE.append(":order"), null, false),
        arguments(matcher("select"), VM_EVALUATE.append(":select"), null, true),
        arguments(matcher("select"), VM_EVALUATE.append(":order"), null, false));
  }

  private static ReportMatcher matcher(String name) {
    return switch (name) {
      case "true" -> (task, logs) -> true;
      case "false" -> (task, logs) -> false;
      default -> ReportMatchers.findMatcher(name).get();
    };
  }
}
