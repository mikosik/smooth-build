package org.smoothbuild.cli.match;

import static org.smoothbuild.virtualmachine.VmConstants.VM_EVALUATE;

import com.google.common.collect.ImmutableMap;
import java.util.function.Predicate;
import org.smoothbuild.common.log.base.LabelMatcher;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.report.Report;
import picocli.CommandLine.TypeConversionException;

public class ReportMatchers {
  public static final Predicate<Report> ALL = (report) -> true;
  public static final Predicate<Report> NONE = (report) -> false;

  static final Predicate<Report> FATAL = logLevelMatcher(Level.FATAL);
  static final Predicate<Report> ERROR = logLevelMatcher(Level.ERROR);
  static final Predicate<Report> WARNING = logLevelMatcher(Level.WARNING);
  static final Predicate<Report> INFO = logLevelMatcher(Level.INFO);

  static final Predicate<Report> DEFAULT =
      or(INFO, labelMatcher(VM_EVALUATE.append(":invoke").toString()));

  private static final ImmutableMap<String, Predicate<Report>> MATCHERS_MAP =
      ImmutableMap.<String, Predicate<Report>>builder()
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

  public static Predicate<Report> labelMatcher(String pattern) {
    var labelMatcher = newLabelMatcher(pattern);
    return (report) -> labelMatcher.test(report.label());
  }

  private static LabelMatcher newLabelMatcher(String pattern) {
    try {
      return new LabelMatcher(pattern);
    } catch (IllegalArgumentException e) {
      throw new TypeConversionException("Illegal label pattern " + pattern + ". " + e.getMessage());
    }
  }

  public static Predicate<Report> findMatcher(String name) {
    var reportMatcher = MATCHERS_MAP.get(name);
    if (reportMatcher == null) {
      throw new TypeConversionException("Unknown matcher '" + name + "'.");
    }
    return reportMatcher;
  }

  public static Predicate<Report> and(Predicate<Report> left, Predicate<Report> right) {
    return (report) -> left.test(report) && right.test(report);
  }

  public static Predicate<Report> or(Predicate<Report> left, Predicate<Report> right) {
    return (report) -> left.test(report) || right.test(report);
  }

  public static Predicate<Report> not(Predicate<Report> matcher) {
    return (report) -> !matcher.test(report);
  }

  private static Predicate<Report> logLevelMatcher(Level level) {
    return (report) -> report.logs().stream().anyMatch(l -> l.level().hasPriorityAtLeast(level));
  }
}
