package org.smoothbuild.cli.taskmatcher;

import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;

import java.util.Objects;
import java.util.Optional;

import org.smoothbuild.cli.console.Level;
import org.smoothbuild.exec.job.TaskKind;
import org.smoothbuild.io.fs.space.Space;

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
  static final TaskMatcher CONVERSION = kindMatcher(TaskKind.CONVERSION);
  static final TaskMatcher FIELD = kindMatcher(TaskKind.FIELD_READ);
  static final TaskMatcher LITERAL = kindMatcher(TaskKind.LITERAL);
  static final TaskMatcher MAP = kindMatcher(TaskKind.MAP);
  static final TaskMatcher REFERENCE = kindMatcher(TaskKind.REFERENCE);
  static final TaskMatcher VALUE = kindMatcher(TaskKind.VALUE);

  static final TaskMatcher DEFAULT = or(AT_LEAST_INFO, and(PRJ, or(or(CALL, VALUE), FIELD)));

  private static final ImmutableMap<String, TaskMatcher> MATCHERS_MAP =
      ImmutableMap.<String, TaskMatcher>builder()
          .put("all", ALL)
          .put("a", ALL)
          .put("default", DEFAULT)
          .put("d", DEFAULT)
          .put("none", NONE)
          .put("n", NONE)

          .put("fatal", AT_LEAST_FATAL)
          .put("f", AT_LEAST_FATAL)
          .put("error", AT_LEAST_ERROR)
          .put("e", AT_LEAST_ERROR)
          .put("warning", AT_LEAST_WARNING)
          .put("w", AT_LEAST_WARNING)
          .put("info", AT_LEAST_INFO)
          .put("i", AT_LEAST_INFO)

          .put("project", PRJ)
          .put("prj", PRJ)
          .put("p", PRJ)
          .put("sdk", SDK)
          .put("s", SDK)

          .put("call", CALL)
          .put("c", CALL)
          .put("conversion", CONVERSION)
          .put("conv", CONVERSION)
          .put("fr", FIELD)
          .put("field", FIELD)
          .put("literal", LITERAL)
          .put("l", LITERAL)
          .put("map", MAP)
          .put("m", MAP)
          .put("r", REFERENCE)
          .put("reference", REFERENCE)
          .put("value", VALUE)
          .put("v", VALUE)

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

  private static TaskMatcher spaceMatcher(Space user) {
    return (task, logs) -> Objects.equals(task.location().file().space(), user);
  }

  private static TaskMatcher kindMatcher(TaskKind buildingCall) {
    return (task, logs) -> Objects.equals(task.kind(), buildingCall);
  }
}
