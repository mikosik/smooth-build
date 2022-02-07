package org.smoothbuild.out.report;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.out.log.Level.ERROR;
import static org.smoothbuild.out.log.Level.FATAL;
import static org.smoothbuild.out.log.Level.INFO;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.out.report.TaskMatchers.ALL;
import static org.smoothbuild.out.report.TaskMatchers.NONE;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.job.job.TaskKind.CALL;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.out.console.Console;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.vm.job.job.TaskInfo;

public class ConsoleReporterTest extends TestingContext {
  private static final String HEADER = "TASK NAME";
  private static final Log FATAL_LOG = Log.fatal("message");
  private static final Log ERROR_LOG = Log.error("message");
  private static final Log WARNING_LOG = Log.warning("message");
  private static final Log INFO_LOG = Log.info("message");

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final Console console = new Console(new PrintWriter(outputStream, true));
  private ConsoleReporter reporter = new ConsoleReporter(console, ALL, INFO);

  @Nested
  class report_non_build_task {
    @Test
    public void when_fatal_level_then_prints_only_fatal_logs() {
      reporter = new ConsoleReporter(console, null, FATAL);
      reporter.report("header", logsWithAllLevels());
      assertThat(outputStream.toString())
          .contains(ConsoleReporter.toText("header", list(FATAL_LOG)));
    }

    @Test
    public void when_error_level_then_prints_fatal_and_error_logs() {
      reporter = new ConsoleReporter(console, null, ERROR);
      reporter.report("header", logsWithAllLevels());
      assertThat(outputStream.toString())
          .contains(ConsoleReporter.toText("header", list(FATAL_LOG, ERROR_LOG)));
    }

    @Test
    public void when_warning_level_then_prints_fatal_error_and_warning_logs() {
      reporter = new ConsoleReporter(console, null, WARNING);
      reporter.report("header", logsWithAllLevels());
      assertThat(outputStream.toString())
          .contains(ConsoleReporter.toText("header", list(FATAL_LOG, ERROR_LOG, WARNING_LOG)));
    }

    @Test
    public void when_info_level_then_prints_all_logs() {
      reporter = new ConsoleReporter(console, null, INFO);
      reporter.report("header", logsWithAllLevels());
      assertThat(outputStream.toString())
          .contains(ConsoleReporter.toText("header", logsWithAllLevels()));
     }
  }

  @Nested
  class report_build_task {
    @Nested
    class when_filter_matches {
      @Test
      public void when_fatal_level_then_prints_only_fatal_logs() {
        reporter = new ConsoleReporter(console, ALL, FATAL);
        reporter.report(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .contains(ConsoleReporter.toText("header", list(FATAL_LOG)));
      }

      @Test
      public void when_error_level_then_prints_fatal_and_error_logs() {
        reporter = new ConsoleReporter(console, ALL, ERROR);
        reporter.report(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .contains(ConsoleReporter.toText("header", list(FATAL_LOG, ERROR_LOG)));
      }

      @Test
      public void when_warning_level_then_prints_fatal_error_and_warning_logs() {
        reporter = new ConsoleReporter(console, ALL, WARNING);
        reporter.report(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .contains(ConsoleReporter.toText("header", list(FATAL_LOG, ERROR_LOG, WARNING_LOG)));
      }

      @Test
      public void when_info_level_then_prints_all_logs() {
        reporter = new ConsoleReporter(console, ALL, INFO);
        reporter.report(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .contains(ConsoleReporter.toText("header", logsWithAllLevels()));
      }
    }

    @Nested
    class when_filter_does_not_match {
      @Test
      public void when_fatal_level_then_prints_nothing() {
        reporter = new ConsoleReporter(console, NONE, FATAL);
        reporter.report(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .isEmpty();
      }

      @Test
      public void when_error_level_then_prints_nothing() {
        reporter = new ConsoleReporter(console, NONE, ERROR);
        reporter.report(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .isEmpty();
      }

      @Test
      public void when_warning_level_then_prints_nothing() {
        reporter = new ConsoleReporter(console, NONE, WARNING);
        reporter.report(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .isEmpty();
      }

      @Test
      public void when_info_level_then_prints_nothing() {
        reporter = new ConsoleReporter(console, NONE, INFO);
        reporter.report(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .isEmpty();
      }
    }
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
