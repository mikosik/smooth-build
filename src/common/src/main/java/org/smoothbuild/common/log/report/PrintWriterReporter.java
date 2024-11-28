package org.smoothbuild.common.log.report;

import static com.google.common.base.Strings.padStart;
import static org.smoothbuild.common.base.Strings.indent;

import java.io.PrintWriter;
import org.smoothbuild.common.log.base.Log;

/**
 * This class is thread-safe.
 */
public class PrintWriterReporter implements Reporter {
  private final PrintWriter printWriter;

  public PrintWriterReporter(PrintWriter printWriter) {
    this.printWriter = printWriter;
  }

  @Override
  public void submit(Report report) {
    printWriter.println(formatReport(report));
    // We need to flush here. Otherwise, Junit test run from intellij won't show any output
    // when it fails. That's because junit doesn't flush System.out.
    printWriter.flush();
  }

  private static String formatReport(Report report) {
    var builder = new StringBuilder(labelPlusOrigin(report));
    if (!(report.trace().isEmpty())) {
      builder.append("\n");
      builder.append(indent(report.trace().toString()));
    }

    for (Log log : report.logs()) {
      builder.append("\n");
      builder.append(formatLog(log));
    }
    return builder.toString();
  }

  private static String labelPlusOrigin(Report report) {
    var labelString = report.label().toString();
    var originString = report.source().toString();
    if (originString.isEmpty()) {
      return labelString;
    } else {
      return labelString + padStart(originString, 79 - labelString.length(), ' ');
    }
  }

  static String formatLog(Log log) {
    return indent(log.toPrettyString());
  }
}
