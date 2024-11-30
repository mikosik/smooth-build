package org.smoothbuild.cli.match;

import static org.smoothbuild.virtualmachine.VmConstants.VM_EVALUATE;

import com.google.common.collect.ImmutableMap;
import org.smoothbuild.common.log.base.LabelMatcher;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.report.ReportMatcher;
import picocli.CommandLine.TypeConversionException;

public class ReportMatchers {
  public static final ReportMatcher ALL = (label, logs) -> true;
  public static final ReportMatcher NONE = (label, logs) -> false;

  static final ReportMatcher FATAL = logLevelMatcher(Level.FATAL);
  static final ReportMatcher ERROR = logLevelMatcher(Level.ERROR);
  static final ReportMatcher WARNING = logLevelMatcher(Level.WARNING);
  static final ReportMatcher INFO = logLevelMatcher(Level.INFO);

  static final ReportMatcher DEFAULT =
      or(INFO, labelMatcher(VM_EVALUATE.append(":invoke").toString()));

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
          .build();

  public static ReportMatcher labelMatcher(String pattern) {
    var labelMatcher = newLabelMatcher(pattern);
    return (label, logs) -> labelMatcher.test(label);
  }

  private static LabelMatcher newLabelMatcher(String pattern) {
    try {
      return new LabelMatcher(pattern);
    } catch (IllegalArgumentException e) {
      throw new TypeConversionException("Illegal label pattern " + pattern + ". " + e.getMessage());
    }
  }

  public static ReportMatcher findMatcher(String name) {
    var reportMatcher = MATCHERS_MAP.get(name);
    if (reportMatcher == null) {
      throw new TypeConversionException("Unknown matcher '" + name + "'.");
    }
    return reportMatcher;
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
}
