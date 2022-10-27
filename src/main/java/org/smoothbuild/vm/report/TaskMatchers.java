package org.smoothbuild.vm.report;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.out.log.Level;
import org.smoothbuild.vm.task.CombineTask;
import org.smoothbuild.vm.task.ConstTask;
import org.smoothbuild.vm.task.NativeCallTask;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.PickTask;
import org.smoothbuild.vm.task.SelectTask;
import org.smoothbuild.vm.task.Task;

import com.google.common.collect.ImmutableMap;

public class TaskMatchers {
  public static final TaskMatcher ALL = (task, logs) -> true;
  public static final TaskMatcher NONE = (task, logs) -> false;

  static final TaskMatcher AT_LEAST_FATAL = logLevelMatcher(FATAL);
  static final TaskMatcher AT_LEAST_ERROR = logLevelMatcher(ERROR);
  static final TaskMatcher AT_LEAST_WARNING = logLevelMatcher(WARNING);
  static final TaskMatcher AT_LEAST_INFO = logLevelMatcher(INFO);

  static final TaskMatcher CALL = kindMatcher(NativeCallTask.class);
  static final TaskMatcher COMBINE = kindMatcher(CombineTask.class);
  static final TaskMatcher CONST = kindMatcher(ConstTask.class);
  static final TaskMatcher ORDER = kindMatcher(OrderTask.class);
  static final TaskMatcher PICK = kindMatcher(PickTask.class);
  static final TaskMatcher SELECT = kindMatcher(SelectTask.class);

  static final TaskMatcher DEFAULT = or(AT_LEAST_INFO, CALL);

  private static final ImmutableMap<String, TaskMatcher> MATCHERS_MAP =
      ImmutableMap.<String, TaskMatcher>builder()
          .put("all", ALL)
          .put("a", ALL)
          .put("default", DEFAULT)
          .put("d", DEFAULT)
          .put("none", NONE)
          .put("n", NONE)

          .put("fatal", AT_LEAST_FATAL)
          .put("lf", AT_LEAST_FATAL)
          .put("error", AT_LEAST_ERROR)
          .put("le", AT_LEAST_ERROR)
          .put("warning", AT_LEAST_WARNING)
          .put("lw", AT_LEAST_WARNING)
          .put("info", AT_LEAST_INFO)
          .put("li", AT_LEAST_INFO)

          .put("call", CALL)
          .put("c", CALL)
          .put("tuple", COMBINE)
          .put("t", COMBINE)
          .put("const", CONST)
          .put("o", CONST)
          .put("array", ORDER)
          .put("r", ORDER)
          .put("pick", PICK)
          .put("p", PICK)
          .put("select", SELECT)
          .put("s", SELECT)

          .build();

  public static Optional<TaskMatcher> findMatcher(String name) {
    return Optional.ofNullable(MATCHERS_MAP.get(name));
  }

  public static TaskMatcher and(TaskMatcher left, TaskMatcher right) {
    return (task, logs) -> left.matches(task, logs) && right.matches(task, logs);
  }

  public static TaskMatcher or(TaskMatcher left, TaskMatcher right) {
    return (task, logs) -> left.matches(task, logs) || right.matches(task, logs);
  }

  private static TaskMatcher logLevelMatcher(Level level) {
    return (task, logs) -> logs
        .stream()
        .anyMatch(l -> l.level().hasPriorityAtLeast(level));
  }

  private static TaskMatcher kindMatcher(Class<? extends Task> taskClass) {
    return (task, logs) -> Objects.equals(task.getClass(), taskClass);
  }
}
