package org.smoothbuild.common.log.report;

import static org.smoothbuild.common.log.report.Report.reportToString;

import java.io.PrintWriter;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.Origin;

/**
 * This class is thread-safe.
 */
public class ReportPrinter {
  private final PrintWriter printWriter;

  public ReportPrinter(PrintWriter printWriter) {
    this.printWriter = printWriter;
  }

  public void print(Label label, Maybe<Trace> trace, Origin origin, List<Log> logs) {
    printWriter.print(reportToString(label, trace, origin, logs));

    // We need to flush here. Otherwise, Junit test run from intellij won't show any output
    // when it fails. That's because junit doesn't flush System.out.
    printWriter.flush();
  }
}
