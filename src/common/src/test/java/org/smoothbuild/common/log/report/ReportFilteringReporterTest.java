package org.smoothbuild.common.log.report;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Origin.DISK;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;

import org.junit.jupiter.api.Test;

public class ReportFilteringReporterTest {
  @Test
  void when_task_matcher_matches_then_report_is_forwarded() {
    testVisibility(true);
  }

  @Test
  void when_task_matcher_not_matches_then_report_is_suppressed() {
    testVisibility(false);
  }

  private void testVisibility(boolean matches) {
    var reportLabel = label("name");
    var logs1 = list(ERROR_LOG);
    var wrappedReporter = mock(Reporter.class);
    var report = report(reportLabel, DISK, logs1);
    var reporter = new ReportFilteringReporter(wrappedReporter, (label, logs) -> matches);

    reporter.submit(report);

    verify(wrappedReporter, times(matches ? 1 : 0)).submit(report);
  }
}
