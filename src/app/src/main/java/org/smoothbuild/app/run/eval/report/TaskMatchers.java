package org.smoothbuild.app.run.eval.report;

import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.Level.FATAL;

import com.google.common.collect.ImmutableMap;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Level;

public class TaskMatchers {
  private static final Label EVALUATING = label("Evaluating");
  public static final TaskMatcher ALL = (task, logs) -> true;
  public static final TaskMatcher NONE = (task, logs) -> false;

  static final TaskMatcher FATAL = logLevelMatcher(Level.FATAL);
  static final TaskMatcher ERROR = logLevelMatcher(Level.ERROR);
  static final TaskMatcher WARNING = logLevelMatcher(Level.WARNING);
  static final TaskMatcher INFO = logLevelMatcher(Level.INFO);

  static final TaskMatcher CALL = labelPrefixMatcher(EVALUATING.append(label("call")));
  static final TaskMatcher COMBINE = labelPrefixMatcher(EVALUATING.append(label("combine")));
  static final TaskMatcher CONST = labelPrefixMatcher(EVALUATING.append(label("const")));
  static final TaskMatcher ORDER = labelPrefixMatcher(EVALUATING.append(label("order")));
  static final TaskMatcher PICK = labelPrefixMatcher(EVALUATING.append(label("pick")));
  static final TaskMatcher SELECT = labelPrefixMatcher(EVALUATING.append(label("select")));

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
          .put("combine", COMBINE)
          .put("const", CONST)
          .put("order", ORDER)
          .put("pick", PICK)
          .put("select", SELECT)
          .build();

  public static Maybe<TaskMatcher> findMatcher(String name) {
    return maybe(MATCHERS_MAP.get(name));
  }

  public static TaskMatcher and(TaskMatcher left, TaskMatcher right) {
    return (label, logs) -> left.matches(label, logs) && right.matches(label, logs);
  }

  public static TaskMatcher or(TaskMatcher left, TaskMatcher right) {
    return (label, logs) -> left.matches(label, logs) || right.matches(label, logs);
  }

  private static TaskMatcher logLevelMatcher(Level level) {
    return (label, logs) -> logs.stream().anyMatch(l -> l.level().hasPriorityAtLeast(level));
  }

  private static TaskMatcher labelPrefixMatcher(Label prefix) {
    return (label, logs) -> label.startsWith(prefix);
  }
}
