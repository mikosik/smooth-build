package org.smoothbuild.app.report;

import static org.smoothbuild.app.report.FormatLog.formatLogs;
import static org.smoothbuild.common.base.Strings.indent;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import java.util.Locale;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.LogCounters;
import org.smoothbuild.common.log.ResultSource;

/**
 * This class is thread-safe.
 */
@Singleton
public class PrintWriterReporter implements Reporter {
  private final PrintWriter printWriter;
  private final LogCounters counters;
  private final Level logLevel;

  @Inject
  public PrintWriterReporter(PrintWriter printWriter, LogCounters counters, Level logLevel) {
    this.printWriter = printWriter;
    this.counters = counters;
    this.logLevel = logLevel;
  }

  public static void printErrorToWriter(PrintWriter printWriter, String message) {
    printWriter.println("smooth: error: " + message);
  }

  @Override
  public void report(
      boolean visible, Label label, String details, ResultSource source, List<Log> logs) {
    increaseCounts(logs);
    if (visible) {
      reportFiltered(label, details, source, logs);
    }
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    increaseCounts(logs);
    reportFiltered(label, details, source, logs);
  }

  private void reportFiltered(Label label, String details, ResultSource source, List<Log> logs) {
    print(label, details, source, logsPassingLevelThreshold(logs));
  }

  private List<Log> logsPassingLevelThreshold(List<Log> logs) {
    return logs.filter(this::passesLevelThreshold);
  }

  private boolean passesLevelThreshold(Log log) {
    return log.level().hasPriorityAtLeast(logLevel);
  }

  private void increaseCounts(List<Log> logs) {
    for (Log log : logs) {
      counters.increment(log.level());
    }
  }

  private void print(Label label, String details, ResultSource source, List<Log> logs) {
    printWriter.println(formatLogs(label, details, source, logs));
  }

  @Override
  public void printSummary() {
    printWriter.println("::Summary");
    int total = 0;
    for (Level level : Level.values()) {
      int count = counters.get(level);
      if (count != 0) {
        int value = counters.get(level);
        printWriter.println(indent(statText(level, value)));
      }
      total += count;
    }
    if (total == 0) {
      printWriter.println("No logs reported");
    }
  }

  @Override
  public void reportResult(String resultMessage) {
    this.printWriter.println(resultMessage);
  }

  private String statText(Level level, int value) {
    var name = level.name().toLowerCase(Locale.ROOT);
    if (1 < value) {
      name = name + "s";
    }
    return value + " " + name;
  }
}
