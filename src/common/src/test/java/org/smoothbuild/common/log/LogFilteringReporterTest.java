package org.smoothbuild.common.log;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.Level.ERROR;
import static org.smoothbuild.common.log.Level.FATAL;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.common.log.Level.WARNING;
import static org.smoothbuild.common.log.ResultSource.DISK;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;
import static org.smoothbuild.common.testing.TestingLog.FATAL_LOG;
import static org.smoothbuild.common.testing.TestingLog.WARNING_LOG;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;

public class LogFilteringReporterTest {
  @ParameterizedTest
  @MethodSource
  void logs_are_filtered(Level level, List<Log> logs, List<Log> expected) {
    var label = label("name");
    var details = "details";
    var resultSource = DISK;
    var wrappedReporter = mock(Reporter.class);
    var reporter = new LogFilteringReporter(wrappedReporter, level);

    reporter.report(label, details, resultSource, logs);

    verify(wrappedReporter).report(label, details, resultSource, expected);
  }

  public static List<Arguments> logs_are_filtered() {
    return list(
        arguments(FATAL, logsWithAllLevels(), list(FATAL_LOG)),
        arguments(ERROR, logsWithAllLevels(), list(FATAL_LOG, ERROR_LOG)),
        arguments(WARNING, logsWithAllLevels(), list(FATAL_LOG, ERROR_LOG, WARNING_LOG)),
        arguments(INFO, logsWithAllLevels(), logsWithAllLevels())
    );
  }
}
