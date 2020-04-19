package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.cli.console.Log.fatal;
import static org.smoothbuild.cli.console.Log.info;
import static org.smoothbuild.cli.console.Log.warning;
import static org.smoothbuild.util.Strings.unlines;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.util.Strings;

@SuppressWarnings("ClassCanBeStatic")
public class ReporterTest extends TestingContext {
  private final String name = "TASK NAME";
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream printStream = new PrintStream(outputStream);
  private final Reporter console = new Reporter(new Console(printStream));

  @Nested
  class print {
    @Test
    public void logs_containing_error() {
      console.report(name, List.of(error("message string")));
      assertThat(outputStream.toString()).isEqualTo(unlines(
          "  TASK NAME",
          "   + ERROR: message string",
          ""));
    }

    @Test
    public void logs_without_error() {
      console.report(name, List.of(warning("message string\nsecond line")));
      assertThat(outputStream.toString()).isEqualTo(unlines(
          "  TASK NAME",
          "   + WARNING: message string",
          "     second line",
          ""));
    }
  }

  @Nested
  class isProblemReported {
    @Test
    public void returns_false_when_nothing_was_logged() {
      assertFalse(console.isProblemReported());
    }

    @Test
    public void returns_false_when_only_info_was_logged() {
      console.report(name, List.of(info("message string")));
      assertFalse(console.isProblemReported());
    }

    @Test
    public void returns_false_when_only_warning_was_logged() {
      console.report(name, List.of(warning("message string")));
      assertFalse(console.isProblemReported());
    }

    @Test
    public void returns_true_when_error_was_logged() {
      console.report(name, List.of(error("message string")));
      assertTrue(console.isProblemReported());
    }

    @Test
    public void returns_true_when_fatal_was_logged() {
      console.report(name, List.of(fatal("message string")));
      assertTrue(console.isProblemReported());
    }
  }

  @Nested
  class printFinalSummary {
    @Test
    public void when_error_was_logged() {
      console.report(name, List.of(error("message string")));
      console.printFinalSummary();

      assertThat(outputStream.toString()).isEqualTo(unlines(
          "  TASK NAME",
          "   + ERROR: message string",
          "Summary",
          "  1 error",
          ""));
    }

    @Test
    public void contains_all_stats() {
      List<Log> logs = new ArrayList<>();
      logs.add(Log.fatal("fatal string"));
      for (int i = 0; i < 2; i++) {
        logs.add(error("error string"));
      }
      for (int i = 0; i < 3; i++) {
        logs.add(warning("warning string"));
      }
      for (int i = 0; i < 4; i++) {
        logs.add(info("info string"));
      }

      console.report(name, logs);
      console.printFinalSummary();


      String builder = Strings.unlines(
          "  TASK NAME",
          "   + FATAL: fatal string",
          "   + ERROR: error string",
          "   + ERROR: error string",
          "   + WARNING: warning string",
          "   + WARNING: warning string",
          "   + WARNING: warning string",
          "   + INFO: info string",
          "   + INFO: info string",
          "   + INFO: info string",
          "   + INFO: info string",
          "Summary",
          "  1 fatal",
          "  2 errors",
          "  3 warnings",
          "  4 infos\n");
      assertEquals(builder, outputStream.toString());
    }
  }
}
