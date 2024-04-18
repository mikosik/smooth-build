package org.smoothbuild.cli.match;

import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.virtualmachine.VirtualMachineConstants.EVALUATE;

import com.google.common.collect.ImmutableMap;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.report.ReportMatcher;

public class ReportMatchers {
  public static final ReportMatcher ALL = (label, logs) -> true;
  public static final ReportMatcher NONE = (label, logs) -> false;

  static final ReportMatcher FATAL = logLevelMatcher(Level.FATAL);
  static final ReportMatcher ERROR = logLevelMatcher(Level.ERROR);
  static final ReportMatcher WARNING = logLevelMatcher(Level.WARNING);
  static final ReportMatcher INFO = logLevelMatcher(Level.INFO);

  static final ReportMatcher INVOKE = labelPrefixMatcher(EVALUATE.append(label("invoke")));
  static final ReportMatcher COMBINE = labelPrefixMatcher(EVALUATE.append(label("combine")));
  static final ReportMatcher CONST = labelPrefixMatcher(EVALUATE.append(label("const")));
  static final ReportMatcher ORDER = labelPrefixMatcher(EVALUATE.append(label("order")));
  static final ReportMatcher PICK = labelPrefixMatcher(EVALUATE.append(label("pick")));
  static final ReportMatcher SELECT = labelPrefixMatcher(EVALUATE.append(label("select")));

  static final ReportMatcher DEFAULT = or(INFO, INVOKE);

  private static final ImmutableMap<String, ReportMatcher> MATCHERS_MAP =
      ImmutableMap.<String, ReportMatcher>builder()
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
          .put("invoke", INVOKE)
          .put("combine", COMBINE)
          .put("const", CONST)
          .put("order", ORDER)
          .put("pick", PICK)
          .put("select", SELECT)
          .build();

  public static Maybe<ReportMatcher> findMatcher(String name) {
    return maybe(MATCHERS_MAP.get(name));
  }

  public static ReportMatcher and(ReportMatcher left, ReportMatcher right) {
    return (label, logs) -> left.matches(label, logs) && right.matches(label, logs);
  }

  public static ReportMatcher or(ReportMatcher left, ReportMatcher right) {
    return (label, logs) -> left.matches(label, logs) || right.matches(label, logs);
  }

  public static ReportMatcher not(ReportMatcher matcher) {
    return (label, logs) -> !matcher.matches(label, logs);
  }

  private static ReportMatcher logLevelMatcher(Level level) {
    return (label, logs) -> logs.stream().anyMatch(l -> l.level().hasPriorityAtLeast(level));
  }

  public static ReportMatcher labelPrefixMatcher(Label prefix) {
    return (label, logs) -> label.startsWith(prefix);
  }
}
