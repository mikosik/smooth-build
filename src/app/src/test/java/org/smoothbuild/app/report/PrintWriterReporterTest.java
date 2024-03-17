package org.smoothbuild.app.report;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.app.report.FormatReport.formatReport;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class PrintWriterReporterTest extends TestingVirtualMachine {
  @Test
  public void report_single_log_prints_log_when_its_level_exceeds_threshold() {
    var systemOut = mock(PrintWriter.class);
    var report = report(label("name"), "details", DISK, logsWithAllLevels());
    var reporter = new PrintWriterReporter(systemOut);

    reporter.report(report);

    verify(systemOut).println(formatReport(report));
  }
}
