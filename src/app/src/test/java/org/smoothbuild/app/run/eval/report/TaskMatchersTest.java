package org.smoothbuild.app.run.eval.report;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.app.run.eval.report.TaskMatchers.and;
import static org.smoothbuild.app.run.eval.report.TaskMatchers.or;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Log.fatal;
import static org.smoothbuild.common.log.Log.info;
import static org.smoothbuild.common.log.Log.warning;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TaskMatchersTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("matcher_matches_cases")
  public void matcher_matches(TaskMatcher matcher, Label label, List<Log> logs, boolean expected) {
    assertThat(matcher.matches(label, logs)).isEqualTo(expected);
  }

  public static List<Arguments> matcher_matches_cases() {
    var t = new TestingVirtualMachine();
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
        arguments(matcher("call"), label("evaluate", "call"), null, true),
        arguments(matcher("call"), label("evaluate", "order"), null, false),
        arguments(matcher("combine"), label("evaluate", "combine"), null, true),
        arguments(matcher("combine"), label("evaluate", "order"), null, false),
        arguments(matcher("const"), label("evaluate", "const"), null, true),
        arguments(matcher("const"), label("evaluate", "order"), null, false),
        arguments(matcher("order"), label("evaluate", "order"), null, true),
        arguments(matcher("order"), label("evaluate", "invoke"), null, false),
        arguments(matcher("pick"), label("evaluate", "pick"), null, true),
        arguments(matcher("pick"), label("evaluate", "order"), null, false),
        arguments(matcher("select"), label("evaluate", "select"), null, true),
        arguments(matcher("select"), label("evaluate", "order"), null, false));
  }

  private static TaskMatcher matcher(String name) {
    return switch (name) {
      case "true" -> (task, logs) -> true;
      case "false" -> (task, logs) -> false;
      default -> TaskMatchers.findMatcher(name).get();
    };
  }
}
