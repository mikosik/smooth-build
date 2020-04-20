package org.smoothbuild.cli.console;

import static com.google.common.collect.Maps.toImmutableEnumMap;
import static java.util.Arrays.stream;
import static org.smoothbuild.cli.console.Level.ERROR;
import static org.smoothbuild.cli.console.Level.FATAL;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.collect.ImmutableMap;

/**
 * This class is thread-safe.
 */
@Singleton
public class Reporter {
  private final Console console;
  private final ImmutableMap<Level, AtomicInteger> counts =
      stream(Level.values()).collect(toImmutableEnumMap(v -> v, v -> new AtomicInteger()));

  @Inject
  public Reporter(Console console) {
    this.console = console;
  }

  public void newSection(String name) {
    console.println(name);
  }

  public void report(String task, List<Log> logs) {
    increaseCounts(logs);
    console.print(task, logs);
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
