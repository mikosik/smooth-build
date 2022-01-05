package org.smoothbuild.cli.console;

import static com.google.common.collect.Maps.toImmutableEnumMap;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.cli.taskmatcher.TaskMatcher;
import org.smoothbuild.vm.job.job.JobInfo;

import com.google.common.collect.ImmutableMap;

/**
 * This class is thread-safe.
 */
@Singleton
public class Reporter {
  private static final String TASK_HEADER_PREFIX = "  ";
  private static final String MESSAGE_FIRST_LINE_PREFIX = "   + ";
  private static final String MESSAGE_OTHER_LINES_PREFIX = "     ";

  private final Console console;
  private final TaskMatcher taskMatcher;
  private final Level logLevel;
  private final ImmutableMap<Level, AtomicInteger> counts =
      stream(Level.values()).collect(toImmutableEnumMap(v -> v, v -> new AtomicInteger()));

  @Inject
  public Reporter(Console console, TaskMatcher taskMatcher, Level logLevel) {
    this.console = console;
    this.taskMatcher = taskMatcher;
    this.logLevel = logLevel;
  }

  public void startNewPhase(String name) {
    console.println(name);
  }

  public void report(JobInfo jobInfo, String taskHeader, List<Log> logs) {
    increaseCounts(logs);
    if (taskMatcher.matches(jobInfo, logs)) {
      reportFiltered(taskHeader, logs);
    }
  }

  public void report(String taskHeader, List<Log> logs) {
    increaseCounts(logs);
    reportFiltered(taskHeader, logs);
  }

  private void reportFiltered(String taskHeader, List<Log> logs) {
    print(taskHeader, filterLogs(logs));
  }

  private List<Log> filterLogs(List<Log> logs) {
    return logs.stream()
        .filter(l -> l.level().hasPriorityAtLeast(logLevel))
        .collect(toList());
  }

  private void increaseCounts(List<Log> logs) {
    for (Log log : logs) {
      increaseCount(log.level());
    }
  }

  private void increaseCount(Level level) {
    counts.get(level).incrementAndGet();
  }

  public boolean isProblemReported() {
    return fatalCount() != 0 || errorCount() != 0;
  }

  private int fatalCount() {
    return counts.get(FATAL).get();
  }

  private int errorCount() {
    return counts.get(ERROR).get();
  }

  public void print(String header, List<Log> logs) {
    console.println(toText(header, logs));
  }

  static String toText(String header, List<Log> logs) {
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

  public void printSummary() {
    console.println("Summary");
    int total = 0;
    for (Level level : Level.values()) {
      int count = counts.get(level).get();
      if (count != 0) {
        int value = counts.get(level).get();
        console.println(formattedHeader(statText(level, value)));
      }
      total += count;
    }
    if (total == 0) {
      print("No logs reported.", list());
    }
  }

  private String statText(Level level, int value) {
    String name = level.name().toLowerCase(Locale.ROOT);
    if (1 < value) {
      name = name + "s";
    }
    return value + " " + name;
  }

  public void printlnRawFatal(String message) {
    counts.get(FATAL).incrementAndGet();
    printlnRaw(message);
  }

  public void printlnRaw(String line) {
    console.println(line);
  }
}
