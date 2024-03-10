package org.smoothbuild.app.report;

import static org.smoothbuild.app.report.FormatLog.formatLogs;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Level;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.Reporter;
import org.smoothbuild.common.log.ResultSource;

/**
 * This class is thread-safe.
 */
@Singleton
public class PrintWriterReporter implements Reporter {
  private final PrintWriter printWriter;
  private final Level logLevel;

  @Inject
  public PrintWriterReporter(PrintWriter printWriter, Level logLevel) {
    this.printWriter = printWriter;
    this.logLevel = logLevel;
  }

  public static void printErrorToWriter(PrintWriter printWriter, String message) {
    printWriter.println("smooth: error: " + message);
  }

  @Override
  public void report(Label label, String details, ResultSource source, List<Log> logs) {
    print(label, details, source, logsPassingLevelThreshold(logs));
  }

  private List<Log> logsPassingLevelThreshold(List<Log> logs) {
    return logs.filter(this::passesLevelThreshold);
  }

  private boolean passesLevelThreshold(Log log) {
    return log.level().hasPriorityAtLeast(logLevel);
  }

  private void print(Label label, String details, ResultSource source, List<Log> logs) {
    printWriter.println(formatLogs(label, details, source, logs));
  }
}
