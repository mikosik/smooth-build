package org.smoothbuild.app.report;

import static org.smoothbuild.app.report.FormatReport.formatReport;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.PrintWriter;
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
}
