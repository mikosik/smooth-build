package org.smoothbuild.cli.console;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static com.google.common.collect.Maps.toImmutableEnumMap;
import static java.util.Arrays.stream;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.cli.console.Level.INFO;
import static org.smoothbuild.cli.console.Level.WARNING;
import static org.smoothbuild.util.Strings.unlines;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;

/**
 * This class is thread-save.
 */
@Singleton
public class Console {
  public static final int MESSAGE_GROUP_NAME_HEADER_LENGTH = 73;

  private static final String TASK_HEADER_PREFIX = "  ";
  private static final String MESSAGE_FIRST_LINE_PREFIX = "   + ";
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  private final PrintStream printStream;
  private final ImmutableMap<Level, AtomicInteger> counts =
      stream(Level.values()).collect(toImmutableEnumMap(v -> v, v -> new AtomicInteger()));

  @Inject
  public Console() {
    this(System.out);
  }

  // visible for testing
  Console(PrintStream printStream) {
    this.printStream = printStream;
  }
  public void errors(java.util.List<?> errors) {
    errors.forEach(this::error);
  }

  public void error(Object error) {
    println("error: " + error.toString());
    counts.get(ERROR).incrementAndGet();
  }

  public void print(String header, List<Log> logs) {
    print(toTextAndIncreaseCounts(header, logs));
  }

  private String toTextAndIncreaseCounts(String header, List<Log> messages) {
    StringBuilder text = new StringBuilder(TASK_HEADER_PREFIX + header + "\n");
    for (Log message : messages) {
      text.append(prefixMultiline(message.level() + ": " + message.message()));
      increaseCount(message.level());
    }
    return text.toString();
  }

  private void increaseCount(Level level) {
    counts.get(level).incrementAndGet();
  }

  public void print(String header, Throwable failure) {
    print(TASK_HEADER_PREFIX + header + "\n" + getStackTraceAsString(failure));
    counts.get(FATAL).incrementAndGet();
  }

  public boolean isProblemReported() {
    return fatalCount() != 0 || errorCount() != 0;
  }

  public void printFinalSummary() {
    print(
        statText(FATAL) +
        statText(ERROR) +
        statText(WARNING) +
        statText(INFO));
  }

  private String statText(Level level) {
    int value = counts.get(level).get();
    if (value != 0) {
      String name = level.name().toLowerCase(Locale.ROOT);
      if (1 < value) {
        name = name + "s";
      }
      return MESSAGE_FIRST_LINE_PREFIX + value + " " + name + "\n";
    } else {
      return "";
    }
  }

  private int fatalCount() {
    return counts.get(FATAL).get();
  }

  private int errorCount() {
    return counts.get(ERROR).get();
  }

  private static String prefixMultiline(String text) {
    String[] lines = text.lines().toArray(String[]::new);
    lines[0] = MESSAGE_FIRST_LINE_PREFIX + lines[0];
    for (int i = 1; i < lines.length; i++) {
      lines[i] = MESSAGE_OTHER_LINES_PREFIX + lines[i];
    }
    return unlines(lines) + "\n";
  }

  public void println(String line) {
    printStream.println(line);
  }

  public void print(String line) {
    printStream.print(line);
  }
}
