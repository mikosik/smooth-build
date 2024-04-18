package org.smoothbuild.cli.report;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.common.base.Strings.indent;
import static org.smoothbuild.common.log.base.Log.containsFailure;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;

/**
 * This class is thread-safe.
 */
@Singleton
public class PrintWriterReporter implements Reporter {
  private final PrintWriter printWriter;

  @Inject
  public PrintWriterReporter(PrintWriter printWriter) {
    this.printWriter = printWriter;
  }

  public static void printErrorToWriter(PrintWriter printWriter, String message) {
    printWriter.println("smooth: error: " + message);
  }

  @Override
  public void report(Report report) {
    printWriter.println(formatReport(report));
  }

  static String formatReport(Report report) {
    var labelString = report.label().toString();
    var builder = new StringBuilder(labelString);
    builder.append(padStart(report.source().toString(), 79 - labelString.length(), ' '));
    if (containsFailure(report.logs()) && !(report.trace().topLine() == null)) {
      builder.append("\n");
      builder.append(indent(report.trace().toString()));
    }

    for (Log log : report.logs()) {
      builder.append("\n");
      builder.append(formatLog(log));
    }
    return builder.toString();
  }

  static String formatLog(Log log) {
    return indent(log.toPrettyString());
  }
}
