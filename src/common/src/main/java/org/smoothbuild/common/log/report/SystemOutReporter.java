package org.smoothbuild.common.log.report;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import org.smoothbuild.common.Constants;

public class SystemOutReporter implements Reporter {
  private final ReportPrinter reportPrinter;

  public SystemOutReporter() {
    var outputStreamWriter = new OutputStreamWriter(System.out, Constants.CHARSET);
    this.reportPrinter = new ReportPrinter(new PrintWriter(new BufferedWriter(outputStreamWriter)));
  }

  @Override
  public void submit(Report report) {
    reportPrinter.print(report.label(), report.trace(), report.origin(), report.logs());
  }
}
