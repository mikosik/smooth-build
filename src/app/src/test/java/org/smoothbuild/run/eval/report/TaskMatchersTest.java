package org.smoothbuild.run.eval.report;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Log.error;
import static org.smoothbuild.common.log.Log.fatal;
import static org.smoothbuild.common.log.Log.info;
import static org.smoothbuild.common.log.Log.warning;
import static org.smoothbuild.run.eval.report.TaskMatchers.and;
import static org.smoothbuild.run.eval.report.TaskMatchers.or;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.evaluate.task.Task;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class TaskMatchersTest extends TestingVirtualMachine {
  @ParameterizedTest
  @MethodSource("matcher_matches_cases")
  public void matcher_matches(TaskMatcher matcher, Task task, List<Log> logs, boolean expected) {
    assertThat(matcher.matches(task, logs)).isEqualTo(expected);
  }

  public static List<Arguments> matcher_matches_cases() throws BytecodeException {
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
        arguments(matcher("call"), t.invokeTask(), null, true),
        arguments(matcher("call"), t.orderTask(), null, false),
        arguments(matcher("tuple"), t.combineTask(), null, true),
        arguments(matcher("tuple"), t.orderTask(), null, false),
        arguments(matcher("const"), t.constTask(), null, true),
        arguments(matcher("const"), t.orderTask(), null, false),
        arguments(matcher("array"), t.orderTask(), null, true),
        arguments(matcher("array"), t.invokeTask(), null, false),
        arguments(matcher("pick"), t.pickTask(), null, true),
        arguments(matcher("pick"), t.orderTask(), null, false),
        arguments(matcher("select"), t.selectTask(), null, true),
        arguments(matcher("select"), t.orderTask(), null, false));
  }

  private static TaskMatcher matcher(String name) {
    return switch (name) {
      case "true" -> (task, logs) -> true;
      case "false" -> (task, logs) -> false;
      default -> TaskMatchers.findMatcher(name).get();
    };
  }
}
