package org.smoothbuild.out.report;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.out.log.TestingLog.ERROR_LOG;
import static org.smoothbuild.out.log.TestingLog.FATAL_LOG;
import static org.smoothbuild.out.log.TestingLog.INFO_LOG;
import static org.smoothbuild.out.log.TestingLog.WARNING_LOG;
import static org.smoothbuild.out.report.FormatLog.formatLog;
import static org.smoothbuild.out.report.FormatLog.formatLogs;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.TestingLog;
import org.smoothbuild.testing.TestContext;

public class SystemOutReporterTest extends TestContext {
  private static final String HEADER = "TASK NAME";

  @ParameterizedTest
  @MethodSource(value = "single_log_cases")
  public void report_single_log_logs_log_when_it_exceeds_threshold(
      Log log, Level level, boolean logged) {
    var systemOut = mock(PrintWriter.class);
    var reporter = new SystemOutReporter(systemOut, level);
    reporter.report(log);
    verify(systemOut, times(logged ? 1 : 0)).println(formatLog(log));
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
    var reporter = new SystemOutReporter(systemOut, level);
    reporter.report(true, "header", TestingLog.logsWithAllLevels());
    verify(systemOut, times(1)).println(formatLogs("header", loggedLogs));
  }

  public static List<Arguments> filtered_logs_cases() {
    return List.of(
        arguments(FATAL, list(FATAL_LOG)),
        arguments(ERROR, list(FATAL_LOG, ERROR_LOG)),
        arguments(WARNING, list(FATAL_LOG, ERROR_LOG, WARNING_LOG)),
        arguments(INFO, list(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG)));
  }

  @Test
  void reportResult() {
    var systemOut = mock(PrintWriter.class);
    var reporter = new SystemOutReporter(systemOut, INFO);
    reporter.reportResult("result message");
    verify(systemOut).println("result message");
  }

  @Nested
  class printSummary {
    @Test
    public void contains_all_stats() {
      doTestSummary(INFO);
    }

    @Test
    public void contains_stats_for_logs_with_level_below_threshold() {
      doTestSummary(ERROR);
    }

    private void doTestSummary(Level logLevel) {
      var systemOut = mock(PrintWriter.class);
      var reporter = new SystemOutReporter(systemOut, logLevel);

      List<Log> logs = new ArrayList<>();
      logs.add(FATAL_LOG);
      for (int i = 0; i < 2; i++) {
        logs.add(ERROR_LOG);
      }
      for (int i = 0; i < 3; i++) {
        logs.add(WARNING_LOG);
      }
      for (int i = 0; i < 4; i++) {
        logs.add(INFO_LOG);
      }

      reporter.report(true, HEADER, listOfAll(logs));
      reporter.printSummary();

      var inOrder = inOrder(systemOut);
      inOrder.verify(systemOut).println("::Summary");
      inOrder.verify(systemOut).println("  1 fatal");
      inOrder.verify(systemOut).println("  2 errors");
      inOrder.verify(systemOut).println("  3 warnings");
      inOrder.verify(systemOut).println("  4 infos");
    }

    @Test
    public void skips_levels_with_zero_logs() {
      var systemOut = mock(PrintWriter.class);
      var reporter = new SystemOutReporter(systemOut, INFO);

      List<Log> logs = new ArrayList<>();
      logs.add(FATAL_LOG);
      for (int i = 0; i < 4; i++) {
        logs.add(INFO_LOG);
      }

      reporter.report(true, HEADER, listOfAll(logs));
      reporter.printSummary();

      var inOrder = inOrder(systemOut);
      inOrder.verify(systemOut).println("::Summary");
      inOrder.verify(systemOut).println("  1 fatal");
      inOrder.verify(systemOut).println("  4 infos");
    }
  }
}
