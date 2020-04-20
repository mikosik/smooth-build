package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.cli.console.Log.info;
import static org.smoothbuild.cli.console.Log.warning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.smoothbuild.testing.TestingContext;

@SuppressWarnings("ClassCanBeStatic")
public class ReporterTest extends TestingContext {
  private static final String TASK = "TASK NAME";
  private final Console console = mock(Console.class);
  private final Reporter reporter = new Reporter(console);

  @Nested
  class print {
    @Test
    public void logs_containing_error() {
      List<Log> logs = List.of(error("message"));
      reporter.report(TASK, logs);
      verify(console, only())
          .print(TASK, logs);
    }

    @Test
    public void logs_without_error() {
      List<Log> logs = List.of(warning("message"));
      reporter.report(TASK, logs);
      verify(console, only())
          .print(TASK, logs);
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
      reporter.report(TASK, List.of(info("message string")));
      assertFalse(reporter.isProblemReported());
    }

    @Test
    public void returns_false_when_only_warning_was_logged() {
      reporter.report(TASK, List.of(warning("message string")));
      assertFalse(reporter.isProblemReported());
    }

    @Test
    public void returns_true_when_error_was_logged() {
      reporter.report(TASK, List.of(error("message string")));
      assertTrue(reporter.isProblemReported());
    }

    @Test
    public void returns_true_when_fatal_was_logged() {
      reporter.report(TASK, List.of(fatal("message string")));
      assertTrue(reporter.isProblemReported());
    }
  }

  @Nested
  class printSummary {
    @Test
    public void contains_all_stats() {
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

      reporter.report(TASK, logs);
      reporter.printSummary();

      @SuppressWarnings("unchecked")
      ArgumentCaptor<Map<Level, AtomicInteger>> captor = ArgumentCaptor.forClass(Map.class);
      verify(console).printSummary(captor.capture());
      Map<Level, AtomicInteger> captured = captor.getValue();
      assertThat(captured.get(FATAL).get()).isEqualTo(1);
      assertThat(captured.get(ERROR).get()).isEqualTo(2);
      assertThat(captured.get(WARNING).get()).isEqualTo(3);
      assertThat(captured.get(INFO).get()).isEqualTo(4);
    }
  }
}
