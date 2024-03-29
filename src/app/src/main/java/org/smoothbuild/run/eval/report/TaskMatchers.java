package org.smoothbuild.run.eval.report;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.out.log.Level;
import org.smoothbuild.vm.evaluate.task.CombineTask;
import org.smoothbuild.vm.evaluate.task.ConstTask;
import org.smoothbuild.vm.evaluate.task.InvokeTask;
import org.smoothbuild.vm.evaluate.task.OrderTask;
import org.smoothbuild.vm.evaluate.task.PickTask;
import org.smoothbuild.vm.evaluate.task.SelectTask;
import org.smoothbuild.vm.evaluate.task.Task;

import com.google.common.collect.ImmutableMap;

public class TaskMatchers {
  public static final TaskMatcher ALL = (task, logs) -> true;
  public static final TaskMatcher NONE = (task, logs) -> false;

  static final TaskMatcher FATAL = logLevelMatcher(Level.FATAL);
  static final TaskMatcher ERROR = logLevelMatcher(Level.ERROR);
  static final TaskMatcher WARNING = logLevelMatcher(Level.WARNING);
  static final TaskMatcher INFO = logLevelMatcher(Level.INFO);

  static final TaskMatcher CALL = kindMatcher(InvokeTask.class);
  static final TaskMatcher COMBINE = kindMatcher(CombineTask.class);
  static final TaskMatcher CONST = kindMatcher(ConstTask.class);
  static final TaskMatcher ORDER = kindMatcher(OrderTask.class);
  static final TaskMatcher PICK = kindMatcher(PickTask.class);
  static final TaskMatcher SELECT = kindMatcher(SelectTask.class);

  static final TaskMatcher DEFAULT = or(INFO, CALL);

  private static final ImmutableMap<String, TaskMatcher> MATCHERS_MAP =
      ImmutableMap.<String, TaskMatcher>builder()
          .put("all", ALL)
          .put("a", ALL)
          .put("default", DEFAULT)
          .put("d", DEFAULT)
          .put("none", NONE)
          .put("n", NONE)

          .put("fatal", FATAL)
          .put("lf", FATAL)
          .put("error", ERROR)
          .put("le", ERROR)
          .put("warning", WARNING)
          .put("lw", WARNING)
          .put("info", INFO)
          .put("li", INFO)

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
