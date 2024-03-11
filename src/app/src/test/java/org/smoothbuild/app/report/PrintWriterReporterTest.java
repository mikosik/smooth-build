package org.smoothbuild.app.report;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.app.report.FormatLog.formatLogs;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.ResultSource.DISK;
import static org.smoothbuild.common.testing.TestingLog.logsWithAllLevels;

import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class PrintWriterReporterTest extends TestingVirtualMachine {
  @Test
  public void report_single_log_prints_log_when_its_level_exceeds_threshold() {
    var label = label("name");
    var details = "details";
    var resultSource = DISK;
    var logs = logsWithAllLevels();
    var systemOut = mock(PrintWriter.class);
    var reporter = new PrintWriterReporter(systemOut);

    reporter.report(label, details, resultSource, logs);

    verify(systemOut).println(formatLogs(label, details, resultSource, logs));
  }
}
