package org.smoothbuild.cli.console;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.util.Strings.unlines;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;

public class ConsoleTest {
  private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
  private final Console console = new Console(new PrintStream(outputStream));

  @Nested
  class print_logs {
    @Test
    public void prints_stat_for_each_level() {
      console.print("My 1st header", List.of(error("my 1st error")));
      console.print("My 2nd header", List.of(error("my 2nd error")));
      assertThat(outputStream.toString())
          .isEqualTo(unlines(
              "  My 1st header",
              "   + ERROR: my 1st error",
              "  My 2nd header",
              "   + ERROR: my 2nd error",
              ""
          ));
    }
  }

  @Nested
  class printSummary {
    @Test
    public void prints_stat_for_each_level() {
      Map<Level, AtomicInteger> counts = ImmutableMap.<Level, AtomicInteger>builder()
          .put(FATAL, new AtomicInteger(1))
          .put(ERROR, new AtomicInteger(2))
          .put(WARNING, new AtomicInteger(3))
          .put(INFO, new AtomicInteger(4))
          .build();

      console.printSummary(counts);

      assertThat(outputStream.toString())
          .isEqualTo(unlines(
              "Summary",
              "  1 fatal",
              "  2 errors",
              "  3 warnings",
              "  4 infos\n"));
    }

    @Test
    public void skips_levels_with_zero_logs() {
      Map<Level, AtomicInteger> counts = ImmutableMap.<Level, AtomicInteger>builder()
          .put(FATAL, new AtomicInteger(1))
          .put(ERROR, new AtomicInteger(0))
          .put(WARNING, new AtomicInteger(0))
          .put(INFO, new AtomicInteger(4))
          .build();

      console.printSummary(counts);

      assertThat(outputStream.toString())
          .isEqualTo(unlines(
              "Summary",
              "  1 fatal",
              "  4 infos\n"));
    }
  }
}
