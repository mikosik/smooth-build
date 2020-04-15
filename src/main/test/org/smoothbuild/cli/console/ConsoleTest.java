package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.cli.console.Log.error;
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

import com.google.common.base.Throwables;

@SuppressWarnings("ClassCanBeStatic")
public class ConsoleTest extends TestingContext {
  private final String name = "GROUP NAME";
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final PrintStream printStream = new PrintStream(outputStream);
  private final Console console = new Console(printStream);

  @Nested
  class print {
    @Test
    public void logs_containing_error() {
      console.show(name, List.of(error("message string")));
      assertThat(outputStream.toString()).isEqualTo(unlines(
          "  GROUP NAME",
          "   + ERROR: message string",
          ""));
    }

    @Test
    public void logs_without_error() {
      console.show(name, List.of(warning("message string\nsecond line")));
      assertThat(outputStream.toString()).isEqualTo(unlines(
          "  GROUP NAME",
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
      console.show(name, List.of(info("message string")));
      assertFalse(console.isProblemReported());
    }

    @Test
    public void returns_false_when_only_warning_was_logged() {
      console.show(name, List.of(warning("message string")));
      assertFalse(console.isProblemReported());
    }

    @Test
    public void returns_true_when_error_was_logged() {
      console.show(name, List.of(error("message string")));
      assertTrue(console.isProblemReported());
    }

    @Test
    public void returns_true_when_fatal_was_logged() {
      console.show(name, new RuntimeException("message string"));
      assertTrue(console.isProblemReported());
    }
  }

  @Nested
  class printFinalSummary {
    @Test
    public void when_error_was_logged() {
      console.show(name, List.of(error("message string")));
      console.printFinalSummary();

      assertThat(outputStream.toString()).isEqualTo(unlines(
          "  GROUP NAME",
          "   + ERROR: message string",
          "Summary",
          "  1 error",
          ""));
    }

    @Test
    public void contains_all_stats() {
      List<Log> logs = new ArrayList<>();
      RuntimeException exception = new RuntimeException("fatal message");
      console.show(name, exception);
      for (int i = 0; i < 2; i++) {
        logs.add(error("error string"));
      }
      for (int i = 0; i < 3; i++) {
        logs.add(warning("warning string"));
      }
      for (int i = 0; i < 4; i++) {
        logs.add(info("info string"));
      }

      console.show(name, logs);
      console.printFinalSummary();

      StringBuilder builder = new StringBuilder();
      builder.append("  GROUP NAME\n");
      builder.append(Throwables.getStackTraceAsString(exception));
      builder.append("  GROUP NAME\n");
      builder.append("   + ERROR: error string\n".repeat(2));
      builder.append("   + WARNING: warning string\n".repeat(3));
      builder.append("   + INFO: info string\n".repeat(4));

      builder.append("Summary\n");
      builder.append("  1 fatal\n");
      builder.append("  2 errors\n");
      builder.append("  3 warnings\n");
      builder.append("  4 infos\n");

      assertEquals(builder.toString(), outputStream.toString());
    }
  }
}
