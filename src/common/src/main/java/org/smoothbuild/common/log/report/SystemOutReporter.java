package org.smoothbuild.common.log.report;

import java.io.PrintWriter;

public class SystemOutReporter implements Reporter {
  private final ReportPrinter reportPrinter;

  public SystemOutReporter() {
    this.reportPrinter = new ReportPrinter(new PrintWriter(System.out));
  }

  @Override
  public void submit(Report report) {
    reportPrinter.print(report.label(), report.trace(), report.origin(), report.logs());
  }
}
