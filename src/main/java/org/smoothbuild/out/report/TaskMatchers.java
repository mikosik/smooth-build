package org.smoothbuild.out.report;

import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.io.fs.space.Space;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.vm.job.job.TaskKind;

import com.google.common.collect.ImmutableMap;

public class TaskMatchers {
  public static final TaskMatcher ALL = (task, logs) -> true;
  public static final TaskMatcher NONE = (task, logs) -> false;

  static final TaskMatcher AT_LEAST_FATAL = logLevelMatcher(FATAL);
  static final TaskMatcher AT_LEAST_ERROR = logLevelMatcher(ERROR);
  static final TaskMatcher AT_LEAST_WARNING = logLevelMatcher(WARNING);
  static final TaskMatcher AT_LEAST_INFO = logLevelMatcher(INFO);

  static final TaskMatcher PRJ = spaceMatcher(Space.PRJ);
  static final TaskMatcher SDK = spaceMatcher(Space.SDK);

  static final TaskMatcher CALL = kindMatcher(TaskKind.CALL);
  static final TaskMatcher COMBINE = kindMatcher(TaskKind.COMBINE);
  static final TaskMatcher CONST = kindMatcher(TaskKind.CONST);
  static final TaskMatcher CONVERT = kindMatcher(TaskKind.CONVERT);
  static final TaskMatcher INVOKE = kindMatcher(TaskKind.INVOKE);
  static final TaskMatcher ORDER = kindMatcher(TaskKind.ORDER);
  static final TaskMatcher SELECT = kindMatcher(TaskKind.SELECT);

  static final TaskMatcher DEFAULT = or(AT_LEAST_INFO, and(PRJ, CALL));

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

          .put("project", PRJ)
          .put("prj", PRJ)
          .put("sdk", SDK)

          .put("call", CALL)
          .put("c", CALL)
          .put("combine", COMBINE)
          .put("b", COMBINE)
          .put("const", CONST)
          .put("t", CONST)
          .put("convert", CONVERT)
          .put("r", CONVERT)
          .put("invoke", INVOKE)
          .put("i", INVOKE)
          .put("order", ORDER)
          .put("o", ORDER)
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

  private static TaskMatcher spaceMatcher(Space space) {
    return (task, logs) -> Objects.equals(task.loc().file().space(), space);
  }

  private static TaskMatcher kindMatcher(TaskKind kind) {
    return (task, logs) -> Objects.equals(task.kind(), kind);
  }
}
