package org.smoothbuild.out.report;

import static com.google.common.collect.Maps.toImmutableEnumMap;
import static java.util.Arrays.stream;
import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Lists.filter;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.Log;

import com.google.common.collect.ImmutableMap;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * This class is thread-safe.
 */
@Singleton
public class ConsoleReporter implements Reporter {
  private static final String MESSAGE_FIRST_LINE_PREFIX = "   + ";
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  private final Console console;
  private final Level logLevel;
  private final ImmutableMap<Level, AtomicInteger> counters = createCounters();

  @Inject
  public ConsoleReporter(Console console, Level logLevel) {
    this.console = console;
    this.logLevel = logLevel;
  }

  @Override
  public void startNewPhase(String name) {
    console.println(name);
  }

  @Override
  public void report(boolean visible, String header, List<Log> logs) {
    increaseCounts(logs);
    if (visible) {
      reportFiltered(header, logs);
    }
  }

  @Override
  public void report(Log log) {
    increaseCount(log.level());
    if (passesLevelThreshold(log)) {
      print(log);
    }
  }

  @Override
  public void report(String header, List<Log> logs) {
    increaseCounts(logs);
    reportFiltered(header, logs);
  }

  private void reportFiltered(String header, List<Log> logs) {
    print(header, logsPassingLevelThreshold(logs));
  }

  private List<Log> logsPassingLevelThreshold(List<Log> logs) {
    return filter(logs, this::passesLevelThreshold);
  }

  private boolean passesLevelThreshold(Log log) {
    return log.level().hasPriorityAtLeast(logLevel);
  }

  private void increaseCounts(List<Log> logs) {
    for (Log log : logs) {
      increaseCount(log.level());
    }
  }

  private void increaseCount(Level level) {
    counters.get(level).incrementAndGet();
  }

  private void print(Log log) {
    console.println(formatLog(log));
  }

  private void print(String header, List<Log> logs) {
    console.println(toText(header, logs));
  }

  // visible for testing
  public static String toText(String header, List<Log> logs) {
    var builder = new StringBuilder(indentHeader(header));
    for (Log log : logs) {
      builder.append(formatLog(log));
    }
    return builder.toString();
  }

  // visible for testing
  static String formatLog(Log log) {
    String[] lines = (log.level() + ": " + log.message()).lines().toArray(String[]::new);
    return System.lineSeparator() + prefixMultiline(lines);
  }

  private static String indentHeader(String header) {
    return indent(header);
  }

  // visible for testing
  public static String prefixMultiline(String[] lines) {
    lines[0] = MESSAGE_FIRST_LINE_PREFIX + lines[0];
    for (int i = 1; i < lines.length; i++) {
      lines[i] = MESSAGE_OTHER_LINES_PREFIX + lines[i];
    }
    return String.join(System.lineSeparator(), lines);
  }

  @Override
  public void printSummary() {
    console.println("Summary");
    int total = 0;
    for (Level level : Level.values()) {
      int count = counters.get(level).get();
      if (count != 0) {
        int value = counters.get(level).get();
        console.println(indentHeader(statText(level, value)));
      }
      total += count;
    }
    if (total == 0) {
      print("No logs reported.", list());
    }
  }

  private String statText(Level level, int value) {
    var name = level.name().toLowerCase(Locale.ROOT);
    if (1 < value) {
      name = name + "s";
    }
    return value + " " + name;
  }

  private static ImmutableMap<Level, AtomicInteger> createCounters() {
    return stream(Level.values())
        .collect(toImmutableEnumMap(v -> v, v -> new AtomicInteger()));
  }
}
