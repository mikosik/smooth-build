package org.smoothbuild.app.report;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.app.report.FormatLog.formatLogs;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.Level.ERROR;
import static org.smoothbuild.common.log.Level.FATAL;
import static org.smoothbuild.common.log.Level.INFO;
import static org.smoothbuild.common.log.Level.WARNING;
import static org.smoothbuild.common.log.ResultSource.EXECUTION;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;
import static org.smoothbuild.common.testing.TestingLog.FATAL_LOG;
import static org.smoothbuild.common.testing.TestingLog.INFO_LOG;
import static org.smoothbuild.common.testing.TestingLog.WARNING_LOG;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import java.io.PrintWriter;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.LogCounters;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class PrintWriterReporterTest extends TestingVirtualMachine {

  @ParameterizedTest
  @MethodSource(value = "single_log_cases")
  public void report_single_log_prints_log_when_its_level_exceeds_threshold(
      Log log, Level level, boolean logged) {
    var systemOut = mock(PrintWriter.class);
    var reporter = new PrintWriterReporter(systemOut, new LogCounters(), level);
    var label = label("name");
    var details = "details";
    reporter.report(label, details, EXECUTION, list(log));
    verify(systemOut, times(logged ? 1 : 0))
        .println(formatLogs(label, details, EXECUTION, list(log)));
  }

  private static List<Arguments> single_log_cases() {
    return List.of(
        arguments(FATAL_LOG, FATAL, true),
        arguments(FATAL_LOG, ERROR, true),
        arguments(FATAL_LOG, WARNING, true),
        arguments(FATAL_LOG, INFO, true),
        arguments(ERROR_LOG, FATAL, false),
        arguments(ERROR_LOG, ERROR, true),
        arguments(ERROR_LOG, WARNING, true),
        arguments(ERROR_LOG, INFO, true),
        arguments(WARNING_LOG, FATAL, false),
        arguments(WARNING_LOG, ERROR, false),
        arguments(WARNING_LOG, WARNING, true),
        arguments(WARNING_LOG, INFO, true),
        arguments(INFO_LOG, FATAL, false),
        arguments(INFO_LOG, ERROR, false),
        arguments(INFO_LOG, WARNING, false),
        arguments(INFO_LOG, INFO, true));
  }

  @ParameterizedTest
  @MethodSource("filtered_logs_cases")
  public void prints_logs_which_exceeds_threshold(Level level, List<Log> loggedLogs) {
    var systemOut = mock(PrintWriter.class);
    var reporter = new PrintWriterReporter(systemOut, new LogCounters(), level);
    var label = label("label-name");
    var details = "details";
    reporter.report(true, label, details, EXECUTION, logsWithAllLevels());
    verify(systemOut, times(1)).println(formatLogs(label, details, EXECUTION, loggedLogs));
  }

  public static List<Arguments> filtered_logs_cases() {
    return List.of(
        arguments(FATAL, list(FATAL_LOG)),
        arguments(ERROR, list(FATAL_LOG, ERROR_LOG)),
        arguments(WARNING, list(FATAL_LOG, ERROR_LOG, WARNING_LOG)),
        arguments(INFO, list(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG)));
  }
}
