package org.smoothbuild.common.log.report;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Level.ERROR;
import static org.smoothbuild.common.log.base.Level.FATAL;
import static org.smoothbuild.common.log.base.Level.INFO;
import static org.smoothbuild.common.log.base.Level.WARNING;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;
import static org.smoothbuild.common.testing.TestingLog.FATAL_LOG;
import static org.smoothbuild.common.testing.TestingLog.WARNING_LOG;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Level;
import org.smoothbuild.common.log.base.Log;

public class LogFilteringReporterTest {
  @ParameterizedTest
  @MethodSource
  void logs_are_filtered(Level level, List<Log> logs, List<Log> expected) {
    var wrappedReporter = mock(Reporter.class);
    var report = new Report(label("name"), "details", DISK, logs);
    var reporter = new LogFilteringReporter(wrappedReporter, level);

    reporter.report(report);

    verify(wrappedReporter).report(report.withLogs(expected));
  }

  public static List<Arguments> logs_are_filtered() {
    return list(
        arguments(FATAL, logsWithAllLevels(), list(FATAL_LOG)),
        arguments(ERROR, logsWithAllLevels(), list(FATAL_LOG, ERROR_LOG)),
        arguments(WARNING, logsWithAllLevels(), list(FATAL_LOG, ERROR_LOG, WARNING_LOG)),
        arguments(INFO, logsWithAllLevels(), logsWithAllLevels()));
  }
}
