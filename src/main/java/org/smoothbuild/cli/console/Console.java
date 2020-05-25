package org.smoothbuild.cli.console;

import static org.smoothbuild.util.Strings.unlines;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is thread-save.
 */
@Singleton
public class Console {
  private static final String TASK_HEADER_PREFIX = "  ";
  private static final String MESSAGE_FIRST_LINE_PREFIX = "   + ";
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  private final PrintStream printStream;

  @Inject
  public Console() {
    this(System.out);
  }

  // visible for testing
  Console(PrintStream printStream) {
    this.printStream = printStream;
  }

  public void errors(List<?> errors) {
    errors.forEach(this::error);
  }

  public void error(Object error) {
    println("smooth: error: " + error.toString());
  }

  public void print(String header, List<Log> logs) {
    println(toText(header, logs));
  }

  private static String toText(String header, List<Log> logs) {
    StringBuilder text = new StringBuilder(formattedHeader(header));
    for (Log log : logs) {
      text.append("\n");
      text.append(prefixMultiline(log.level() + ": " + log.message()));
    }
    return text.toString();
  }

  private static String formattedHeader(String header) {
    return TASK_HEADER_PREFIX + header;
  }

  private static String prefixMultiline(String text) {
    String[] lines = text.lines().toArray(String[]::new);
    return prefixMultiline(lines);
  }

  // visible for testing
  public static String prefixMultiline(String[] lines) {
    lines[0] = MESSAGE_FIRST_LINE_PREFIX + lines[0];
    for (int i = 1; i < lines.length; i++) {
      lines[i] = MESSAGE_OTHER_LINES_PREFIX + lines[i];
    }
    return unlines(lines);
  }

  public void printSummary(Map<Level, AtomicInteger> counts) {
    println("Summary");
    int total = 0;
    for (Level level : Level.values()) {
      int count = counts.get(level).get();
      if (count != 0) {
        int value = counts.get(level).get();
        println(formattedHeader(statText(level, value)));
      }
      total += count;
    }
    if (total == 0) {
      print("No logs reported.", List.of());
    }
  }

  private String statText(Level level, int value) {
    String name = level.name().toLowerCase(Locale.ROOT);
    if (1 < value) {
      name = name + "s";
    }
    return value + " " + name;
  }

  public void println(String line) {
    printStream.println(line);
  }
}
