package org.smoothbuild.common.log.report;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.DISK;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;

import org.junit.jupiter.api.Test;

public class FilteringReporterTest {
  @Test
  public void when_task_matcher_matches_then_report_is_forwarded() {
    testVisibility(true);
  }

  @Test
  public void when_task_matcher_not_matches_then_report_is_suppressed() {
    testVisibility(false);
  }

  private void testVisibility(boolean matches) {
    var reportLabel = label("name");
    var details = "details";
    var logs1 = list(ERROR_LOG);
    var wrappedReporter = mock(Reporter.class);
    var reporter = new FilteringReporter(wrappedReporter, (label, logs) -> matches);

    reporter.report(reportLabel, details, DISK, logs1);

    verify(wrappedReporter, times(matches ? 1 : 0)).report(reportLabel, details, DISK, logs1);
  }
}
