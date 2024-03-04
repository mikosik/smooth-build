package org.smoothbuild.out.report;

import static com.google.common.collect.Maps.toImmutableEnumMap;
import static java.util.Arrays.stream;
import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.out.report.FormatLog.formatLog;
import static org.smoothbuild.out.report.FormatLog.formatLogs;

import com.google.common.collect.ImmutableMap;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.Log;

/**
 * This class is thread-safe.
 */
@Singleton
public class SystemOutReporter implements Reporter {
  private final PrintWriter systemOut;
  private final Level logLevel;
  private final ImmutableMap<Level, AtomicInteger> counters = createCounters();

  @Inject
  public SystemOutReporter(PrintWriter systemOut, Level logLevel) {
    this.systemOut = systemOut;
    this.logLevel = logLevel;
  }

  public static void printErrorToWriter(PrintWriter printWriter, String message) {
    printWriter.println("smooth: error: " + message);
  }

  @Override
  public void startNewPhase(String name) {
    systemOut.println(name);
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
    return logs.filter(this::passesLevelThreshold);
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
    systemOut.println(formatLog(log));
  }

  private void print(String header, List<Log> logs) {
    systemOut.println(formatLogs(header, logs));
  }

  @Override
  public void printSummary() {
    systemOut.println("::Summary");
    int total = 0;
    for (Level level : Level.values()) {
      int count = counters.get(level).get();
      if (count != 0) {
        int value = counters.get(level).get();
        systemOut.println(indent(statText(level, value)));
      }
      total += count;
    }
    if (total == 0) {
      print("No logs reported.", list());
    }
  }

  @Override
  public void reportResult(String resultMessage) {
    this.systemOut.println(resultMessage);
  }

  private String statText(Level level, int value) {
    var name = level.name().toLowerCase(Locale.ROOT);
    if (1 < value) {
      name = name + "s";
    }
    return value + " " + name;
  }

  private static ImmutableMap<Level, AtomicInteger> createCounters() {
    return stream(Level.values()).collect(toImmutableEnumMap(v -> v, v -> new AtomicInteger()));
  }
}
