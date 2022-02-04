package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.cli.console.ConsoleReporter.toText;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.cli.console.Log.info;
import static org.smoothbuild.cli.console.Log.warning;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.ALL;
import static org.smoothbuild.cli.taskmatcher.TaskMatchers.NONE;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.job.job.TaskKind.CALL;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.vm.job.job.TaskInfo;

public class ConsoleReporterTest extends TestingContext {
  private static final String HEADER = "TASK NAME";
  private static final Log FATAL_LOG = fatal("message");
  private static final Log ERROR_LOG = error("message");
  private static final Log WARNING_LOG = warning("message");
  private static final Log INFO_LOG = info("message");

  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final Console console = new Console(new PrintWriter(outputStream, true));
  private Reporter reporter = new ConsoleReporter(console, ALL, INFO);

  @Nested
  class report_non_build_task {
    @Test
    public void when_fatal_level_then_prints_only_fatal_logs() {
      reporter = new ConsoleReporter(console, null, FATAL);
      reporter.report("header", logsWithAllLevels());
      assertThat(outputStream.toString())
          .contains(toText("header", list(FATAL_LOG)));
    }

    @Test
    public void when_error_level_then_prints_fatal_and_error_logs() {
      reporter = new ConsoleReporter(console, null, ERROR);
      reporter.report("header", logsWithAllLevels());
      assertThat(outputStream.toString())
          .contains(toText("header", list(FATAL_LOG, ERROR_LOG)));
    }

    @Test
    public void when_warning_level_then_prints_fatal_error_and_warning_logs() {
      reporter = new ConsoleReporter(console, null, WARNING);
      reporter.report("header", logsWithAllLevels());
      assertThat(outputStream.toString())
          .contains(toText("header", list(FATAL_LOG, ERROR_LOG, WARNING_LOG)));
    }

    @Test
    public void when_info_level_then_prints_all_logs() {
      reporter = new ConsoleReporter(console, null, INFO);
      reporter.report("header", logsWithAllLevels());
      assertThat(outputStream.toString())
          .contains(toText("header", logsWithAllLevels()));
     }
  }

  @Nested
  class report_build_task {
    @Nested
    class when_filter_matches {
      @Test
      public void when_fatal_level_then_prints_only_fatal_logs() {
        reporter = new ConsoleReporter(console, ALL, FATAL);
        reporter.reportTask(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .contains(toText("header", list(FATAL_LOG)));
      }

      @Test
      public void when_error_level_then_prints_fatal_and_error_logs() {
        reporter = new ConsoleReporter(console, ALL, ERROR);
        reporter.reportTask(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .contains(toText("header", list(FATAL_LOG, ERROR_LOG)));
      }

      @Test
      public void when_warning_level_then_prints_fatal_error_and_warning_logs() {
        reporter = new ConsoleReporter(console, ALL, WARNING);
        reporter.reportTask(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .contains(toText("header", list(FATAL_LOG, ERROR_LOG, WARNING_LOG)));
      }

      @Test
      public void when_info_level_then_prints_all_logs() {
        reporter = new ConsoleReporter(console, ALL, INFO);
        reporter.reportTask(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .contains(toText("header", logsWithAllLevels()));
      }
    }

    @Nested
    class when_filter_does_not_match {
      @Test
      public void when_fatal_level_then_prints_nothing() {
        reporter = new ConsoleReporter(console, NONE, FATAL);
        reporter.reportTask(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .isEmpty();
      }

      @Test
      public void when_error_level_then_prints_nothing() {
        reporter = new ConsoleReporter(console, NONE, ERROR);
        reporter.reportTask(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .isEmpty();
      }

      @Test
      public void when_warning_level_then_prints_nothing() {
        reporter = new ConsoleReporter(console, NONE, WARNING);
        reporter.reportTask(taskInfo(), "header", logsWithAllLevels());
        assertThat(outputStream.toString())
            .isEmpty();
      }

      @Test
      public void when_info_level_then_prints_nothing() {
        reporter = new ConsoleReporter(console, NONE, INFO);
        reporter.reportTask(taskInfo(), "header", logsWithAllLevels());
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
      Reporter reporter = new ConsoleReporter(console, ALL, logLevel);

      List<Log> logs = new ArrayList<>();
      logs.add(fatal("fatal string"));
      for (int i = 0; i < 2; i++) {
        logs.add(error("error string"));
      }
      for (int i = 0; i < 3; i++) {
        logs.add(warning("warning string"));
      }
      for (int i = 0; i < 4; i++) {
        logs.add(info("info string"));
      }

      reporter.reportTask(taskInfo(), HEADER, logs);
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
      Reporter reporter = new ConsoleReporter(console, ALL, INFO);

      List<Log> logs = new ArrayList<>();
      logs.add(fatal("fatal string"));
      for (int i = 0; i < 4; i++) {
        logs.add(info("info string"));
      }

      reporter.reportTask(taskInfo(), HEADER, logs);
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
