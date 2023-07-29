package org.smoothbuild.out.report;

import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.out.report.ConsoleReporter.formatLog;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.testing.TestContext;

public class ConsoleReporterTest extends TestContext {
  private static final String HEADER = "TASK NAME";
  private static final Log FATAL_LOG = Log.fatal("fatal message");
  private static final Log ERROR_LOG = Log.error("error message");
  private static final Log WARNING_LOG = Log.warning("warning message");
  private static final Log INFO_LOG = Log.info("info message");

  @ParameterizedTest
  @MethodSource(value = "single_log_cases")
  public void report_single_log_logs_log_when_it_exceeds_threshold(Log log, Level level,
      boolean logged) {
    var console = mock(Console.class);
    var reporter = new ConsoleReporter(console, level);
    reporter.report(log);
    if (logged) {
      verify(console, times(1))
          .println(formatLog(log));
    } else {
      verify(console, times(0))
          .println(formatLog(log));
    }
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
        arguments(INFO_LOG, INFO, true)
    );
  }

  @ParameterizedTest
  @MethodSource("filtered_logs_cases")
  public void prints_logs_which_exceeds_threshold(Level level, List<Log> loggedLogs) {
    var console = mock(Console.class);
    var reporter = new ConsoleReporter(console, level);
    reporter.report(true, "header", logsWithAllLevels());
    verify(console, times(1))
        .println(ConsoleReporter.toText("header", loggedLogs));
  }

  public static List<Arguments> filtered_logs_cases() {
    return List.of(
        arguments(FATAL, list(FATAL_LOG)),
        arguments(ERROR, list(FATAL_LOG, ERROR_LOG)),
        arguments(WARNING, list(FATAL_LOG, ERROR_LOG, WARNING_LOG)),
        arguments(INFO, list(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG))
    );
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
      var console = mock(Console.class);
      var reporter = new ConsoleReporter(console, logLevel);

      List<Log> logs = new ArrayList<>();
      logs.add(Log.fatal("fatal string"));
      for (int i = 0; i < 2; i++) {
        logs.add(Log.error("error string"));
      }
      for (int i = 0; i < 3; i++) {
        logs.add(Log.warning("warning string"));
      }
      for (int i = 0; i < 4; i++) {
        logs.add(Log.info("info string"));
      }

      reporter.report(true, HEADER, logs);
      reporter.printSummary();

      var inOrder = inOrder(console);
      inOrder.verify(console).println("Summary");
      inOrder.verify(console).println("  1 fatal");
      inOrder.verify(console).println("  2 errors");
      inOrder.verify(console).println("  3 warnings");
      inOrder.verify(console).println("  4 infos");
    }

    @Test
    public void skips_levels_with_zero_logs() {
      var console = mock(Console.class);
      var reporter = new ConsoleReporter(console, INFO);

      List<Log> logs = new ArrayList<>();
      logs.add(Log.fatal("fatal string"));
      for (int i = 0; i < 4; i++) {
        logs.add(Log.info("info string"));
      }

      reporter.report(true, HEADER, logs);
      reporter.printSummary();

      var inOrder = inOrder(console);
      inOrder.verify(console).println("Summary");
      inOrder.verify(console).println("  1 fatal");
      inOrder.verify(console).println("  4 infos");
    }
  }

  private static List<Log> logsWithAllLevels() {
    return list(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG);
  }
}
