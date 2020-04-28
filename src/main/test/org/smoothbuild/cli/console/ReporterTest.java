package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.testing.TestingContext;

@SuppressWarnings("ClassCanBeStatic")
public class ReporterTest extends TestingContext {
  private static final String HEADER = "TASK NAME";
  private static final Log FATAL_LOG = fatal("message");
  private static final Log ERROR_LOG = error("message");
  private static final Log WARNING_LOG = warning("message");
  private static final Log INFO_LOG = info("message");
  private final Console console = mock(Console.class);
  private Reporter reporter = new Reporter(console, ALL, INFO);

  @Nested
  class report_non_build_task {
    @Test
    public void when_fatal_level_then_prints_only_fatal_logs() {
      reporter = new Reporter(console, null, FATAL);
      reporter.report("header", logsWithAllLevels());
      verify(console, only())
          .print("header", List.of(FATAL_LOG));
    }

    @Test
    public void when_error_level_then_prints_fatal_and_error_logs() {
      reporter = new Reporter(console, null, ERROR);
      reporter.report("header", logsWithAllLevels());
      verify(console, only())
          .print("header", List.of(FATAL_LOG, ERROR_LOG));
    }

    @Test
    public void when_warning_level_then_prints_fatal_error_and_warning_logs() {
      reporter = new Reporter(console, null, WARNING);
      reporter.report("header", logsWithAllLevels());
      verify(console, only())
          .print("header", List.of(FATAL_LOG, ERROR_LOG, WARNING_LOG));
     }

    @Test
    public void when_info_level_then_prints_all_logs() {
      reporter = new Reporter(console, null, INFO);
      reporter.report("header", logsWithAllLevels());
      verify(console, only())
          .print("header", logsWithAllLevels());
     }
  }

  @Nested
  class report_build_task {
    @Nested
    class when_filter_matches {
      @Test
      public void when_fatal_level_then_prints_only_fatal_logs() {
        reporter = new Reporter(console, ALL, FATAL);
        reporter.report(task(), "header", logsWithAllLevels());
        verify(console, only())
            .print("header", List.of(FATAL_LOG));
      }

      @Test
      public void when_error_level_then_prints_fatal_and_error_logs() {
        reporter = new Reporter(console, ALL, ERROR);
        reporter.report(task(), "header", logsWithAllLevels());
        verify(console, only())
            .print("header", List.of(FATAL_LOG, ERROR_LOG));
      }

      @Test
      public void when_warning_level_then_prints_fatal_error_and_warning_logs() {
        reporter = new Reporter(console, ALL, WARNING);
        reporter.report(task(), "header", logsWithAllLevels());
        verify(console, only())
            .print("header", List.of(FATAL_LOG, ERROR_LOG, WARNING_LOG));
      }

      @Test
      public void when_info_level_then_prints_all_logs() {
        reporter = new Reporter(console, ALL, INFO);
        reporter.report(task(), "header", logsWithAllLevels());
        verify(console, only())
            .print("header", logsWithAllLevels());
      }
    }

    @Nested
    class when_filter_does_not_match {
      @Test
      public void when_fatal_level_then_prints_nothing() {
        reporter = new Reporter(console, NONE, FATAL);
        reporter.report(task(), "header", logsWithAllLevels());
        verifyNoInteractions(console);
      }

      @Test
      public void when_error_level_then_prints_nothing() {
        reporter = new Reporter(console, NONE, ERROR);
        reporter.report(task(), "header", logsWithAllLevels());
        verifyNoInteractions(console);
      }

      @Test
      public void when_warning_level_then_prints_nothing() {
        reporter = new Reporter(console, NONE, WARNING);
        reporter.report(task(), "header", logsWithAllLevels());
        verifyNoInteractions(console);
      }

      @Test
      public void when_info_level_then_prints_nothing() {
        reporter = new Reporter(console, NONE, INFO);
        reporter.report(task(), "header", logsWithAllLevels());
        verifyNoInteractions(console);
      }
    }
  }

  @Nested
  class isProblemReported {
    @Test
    public void returns_false_when_nothing_was_logged() {
      assertFalse(reporter.isProblemReported());
    }

    @Test
    public void returns_false_when_only_info_was_logged() {
      reporter.report(task(), HEADER, List.of(info("message string")));
      assertFalse(reporter.isProblemReported());
    }

    @Test
    public void returns_false_when_only_warning_was_logged() {
      reporter.report(task(), HEADER, List.of(warning("message string")));
      assertFalse(reporter.isProblemReported());
    }

    @Test
    public void returns_true_when_error_was_logged() {
      reporter.report(task(), HEADER, List.of(error("message string")));
      assertTrue(reporter.isProblemReported());
    }

    @Test
    public void returns_true_when_fatal_was_logged() {
      reporter.report(task(), HEADER, List.of(fatal("message string")));
      assertTrue(reporter.isProblemReported());
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
      Reporter reporter = new Reporter(console, ALL, logLevel);

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

      reporter.report(task(), HEADER, logs);
      reporter.printSummary();

      @SuppressWarnings("unchecked")
      ArgumentCaptor<Map<Level, AtomicInteger>> captor = ArgumentCaptor.forClass(Map.class);
      Mockito.verify(console).printSummary(captor.capture());
      Map<Level, AtomicInteger> captured = captor.getValue();
      assertThat(captured.get(FATAL).get()).isEqualTo(1);
      assertThat(captured.get(ERROR).get()).isEqualTo(2);
      assertThat(captured.get(Level.WARNING).get()).isEqualTo(3);
      assertThat(captured.get(INFO).get()).isEqualTo(4);
    }
  }

  private static List<Log> logsWithAllLevels() {
    return List.of(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG);
  }

  private static Task task() {
    return mock(Task.class);
  }
}
