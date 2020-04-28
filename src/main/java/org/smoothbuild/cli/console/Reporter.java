package org.smoothbuild.cli.console;

import static com.google.common.collect.Maps.toImmutableEnumMap;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.cli.taskmatcher.TaskMatcher;
import org.smoothbuild.exec.task.base.Task;

import com.google.common.collect.ImmutableMap;

/**
 * This class is thread-safe.
 */
@Singleton
public class Reporter {
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

  public void report(Task task, String taskHeader, List<Log> logs) {
    increaseCounts(logs);
    if (taskMatcher.matches(task, logs)) {
      reportFiltered(taskHeader, logs);
    }
  }

  public void report(String taskHeader, List<Log> logs) {
    increaseCounts(logs);
    reportFiltered(taskHeader, logs);
  }

  private void reportFiltered(String taskHeader, List<Log> logs) {
    console.print(taskHeader, filterLogs(logs));
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

  public void printSummary() {
    console.printSummary(counts);
  }

  private int fatalCount() {
    return counts.get(FATAL).get();
  }

  private int errorCount() {
    return counts.get(ERROR).get();
  }

  public void printlnRaw(String line) {
    console.println(line);
  }
}
