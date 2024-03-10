package org.smoothbuild.common.log;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.Level.ERROR;
import static org.smoothbuild.common.log.Level.FATAL;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.common.log.Level.WARNING;
import static org.smoothbuild.common.log.ResultSource.DISK;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;
import static org.smoothbuild.common.testing.TestingLog.FATAL_LOG;
import static org.smoothbuild.common.testing.TestingLog.INFO_LOG;
import static org.smoothbuild.common.testing.TestingLog.WARNING_LOG;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class CountingReporterTest {
  @Test
  void report_call_is_forwarded_to_wrapped_reporter() {
    var logCounters = new LogCounters();
    var stepReporter = mock(Reporter.class);
    var countingReporter = new CountingReporter(stepReporter, logCounters);

    var label = label("name");
    var details = "details";
    countingReporter.report(label, details, DISK, list(ERROR_LOG));

    verify(stepReporter).report(label, details, DISK, list(ERROR_LOG));
  }

  @ParameterizedTest
  @MethodSource
  void counters_are_incremented(Log log, Level reportedLevel) {
    var logCounters = mock(LogCounters.class);
    var stepReporter = mock(Reporter.class);
    var countingReporter = new CountingReporter(stepReporter, logCounters);

    var label = label("name");
    var details = "details";
    countingReporter.report(label, details, DISK, list(log));

    verify(logCounters).increment(reportedLevel);
    verifyNoMoreInteractions(logCounters);
  }

  public static List<Arguments> counters_are_incremented() {
    return List.of(
        arguments(FATAL_LOG, FATAL),
        arguments(ERROR_LOG, ERROR),
        arguments(WARNING_LOG, WARNING),
        arguments(INFO_LOG, INFO));
  }
}
