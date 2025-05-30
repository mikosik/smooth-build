package org.smoothbuild.common.log.report;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;
import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;
import static org.smoothbuild.common.testing.TestingLog.FATAL_LOG;
import static org.smoothbuild.common.testing.TestingLog.WARNING_LOG;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dagger.CommonTestContext;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.base.Log;

public class FilteringReporterTest extends CommonTestContext {
  @Test
  void when_task_filter_matches_then_task_is_printed() {
    var reportPrinter = mock(ReportPrinter.class);
    var reporter = new FilteringReporter(reportPrinter, r -> true, r -> true, INFO);

    reporter.submit(report(aLabel(), aTrace(), DISK, logs()));

    verify(reportPrinter).print(aLabel(), aTrace(), DISK, logs());
  }

  @Test
  void when_task_filter_not_matches_then_task_is_not_printed() {
    var reportPrinter = mock(ReportPrinter.class);
    var reporter = new FilteringReporter(reportPrinter, r -> false, r -> true, INFO);

    reporter.submit(report(aLabel(), aTrace(), DISK, logs()));

    verifyNoInteractions(reportPrinter);
  }

  @Test
  void when_trace_filter_matches_then_trace_is_printed() {
    var reportPrinter = mock(ReportPrinter.class);
    var reporter = new FilteringReporter(reportPrinter, r -> true, r -> true, INFO);

    reporter.submit(report(aLabel(), aTrace(), DISK, logs()));

    verify(reportPrinter).print(aLabel(), aTrace(), DISK, logs());
  }

  @Test
  void when_trace_filter_not_matches_then_trace_is_not_printed() {
    var reportPrinter = mock(ReportPrinter.class);
    var reporter = new FilteringReporter(reportPrinter, r -> true, r -> false, INFO);

    reporter.submit(report(aLabel(), aTrace(), DISK, logs()));

    verify(reportPrinter).print(aLabel(), none(), DISK, logs());
  }

  @ParameterizedTest
  @MethodSource
  void logs_that_match_level_or_above_are_printed(Level level, List<Log> logs, List<Log> expected) {
    var wrappedReporter = mock(ReportPrinter.class);
    var report = report(aLabel(), aTrace(), DISK, logs);
    var reporter = new FilteringReporter(wrappedReporter, r -> true, r -> true, level);

    reporter.submit(report);

    verify(wrappedReporter).print(aLabel(), aTrace(), DISK, expected);
  }

  public static List<Arguments> logs_that_match_level_or_above_are_printed() {
    return list(
        arguments(FATAL, logsWithAllLevels(), list(FATAL_LOG)),
        arguments(ERROR, logsWithAllLevels(), list(FATAL_LOG, ERROR_LOG)),
        arguments(WARNING, logsWithAllLevels(), list(FATAL_LOG, ERROR_LOG, WARNING_LOG)),
        arguments(INFO, logsWithAllLevels(), logsWithAllLevels()));
  }

  private static Label aLabel() {
    return label(":name");
  }

  private Maybe<Trace> aTrace() {
    return some(trace("call", 3));
  }

  private static List<Log> logs() {
    return logsWithAllLevels();
  }
}
