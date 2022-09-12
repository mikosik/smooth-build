package org.smoothbuild.out.report;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.out.report.TaskMatchers.ALL;
import static org.smoothbuild.out.report.TaskMatchers.NONE;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.execute.TaskKind.CALL;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.out.console.Console;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.execute.TaskInfo;

public class ConsoleReporterTest extends TestContext {
  private static final String HEADER = "TASK NAME";
  private static final Log FATAL_LOG = Log.fatal("fatal message");
  private static final Log ERROR_LOG = Log.error("error message");
  private static final Log WARNING_LOG = Log.warning("warning message");
  private static final Log INFO_LOG = Log.info("info message");

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final Console console = new Console(new PrintWriter(outputStream, true));
  private ConsoleReporter reporter = new ConsoleReporter(console, ALL, INFO);

  @ParameterizedTest
  @MethodSource(value = "single_log_cases")
  public void report_single_log_logs_log_when_it_passes_threshold(Log log, Level level,
      boolean logged) {
    reporter = new ConsoleReporter(console, null, level);
    reporter.report(log);
    if (logged) {
      assertThat(outputStream.toString())
          .contains(ConsoleReporter.formatLog(log));
    } else {
      assertThat(outputStream.toString())
          .doesNotContain(ConsoleReporter.formatLog(log));
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
  public void report_non_build_task_logs_logs_which_passes_threshold(Level level,
      List<Log> loggedLogs) {
    reporter = new ConsoleReporter(console, null, level);
    reporter.report("header", logsWithAllLevels());
    assertThat(outputStream.toString())
        .contains(ConsoleReporter.toText("header", loggedLogs));
  }

  @ParameterizedTest
  @MethodSource("filtered_logs_cases")
  public void when_filter_matches_then_logs_which_passes_threshold_are_logged(
      Level level, List<Log> loggedLogs) {
    reporter = new ConsoleReporter(console, ALL, level);
    reporter.report(taskInfo(), "header", logsWithAllLevels());
    assertThat(outputStream.toString())
        .contains(ConsoleReporter.toText("header", loggedLogs));
  }

  public static List<Arguments> filtered_logs_cases() {
    return List.of(
        arguments(FATAL, list(FATAL_LOG)),
        arguments(ERROR, list(FATAL_LOG, ERROR_LOG)),
        arguments(WARNING, list(FATAL_LOG, ERROR_LOG, WARNING_LOG)),
        arguments(INFO, list(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG))
    );
  }

  @ParameterizedTest
  @MethodSource("all_levels")
  public void when_filter_doesnt_match_then_no_log_is_logged(Level level) {
    reporter = new ConsoleReporter(console, NONE, level);
    reporter.report(taskInfo(), "header", logsWithAllLevels());
    assertThat(outputStream.toString())
        .isEmpty();
  }

  private static List<Level> all_levels() {
    return List.of(Level.values());
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
      var reporter = new ConsoleReporter(console, ALL, logLevel);

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

      reporter.report(taskInfo(), HEADER, logs);
      reporter.printSummary();

      assertThat(outputStream.toString())
          .contains(unlines(
              "Summary",
              "  1 fatal",
              "  2 errors",
              "  3 warnings",
              "  4 infos",
              ""));
    }

    @Test
    public void skips_levels_with_zero_logs() {
      var reporter = new ConsoleReporter(console, ALL, INFO);

      List<Log> logs = new ArrayList<>();
      logs.add(Log.fatal("fatal string"));
      for (int i = 0; i < 4; i++) {
        logs.add(Log.info("info string"));
      }

      reporter.report(taskInfo(), HEADER, logs);
      reporter.printSummary();

      assertThat(outputStream.toString())
          .contains(unlines(
              "Summary",
              "  1 fatal",
              "  4 infos\n"));
    }
  }

  private static List<Log> logsWithAllLevels() {
    return list(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG);
  }

  private static TaskInfo taskInfo() {
    return new TaskInfo(CALL, "name", loc());
  }
}
