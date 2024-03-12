package org.smoothbuild.app.report;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.app.run.eval.report.EvaluateConstants.EVALUATE;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.ALL;
import static org.smoothbuild.app.run.eval.report.ReportMatchers.NONE;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.ResultSource.DISK;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.ReportMatcher;
import org.smoothbuild.common.log.Reporter;

public class TaskFilteringReporterTest {
  @Test
  public void when_task_matcher_matches_then_report_is_forwarded() {
    testVisibility(ALL, true, EVALUATE.append(label("name")));
  }

  @Test
  public void when_task_matcher_not_matches_then_report_is_suppressed() {
    testVisibility(NONE, false, EVALUATE.append(label("name")));
  }

  private void testVisibility(ReportMatcher reportMatcher, boolean matched, Label label) {
    var details = "details";
    var logs = list(ERROR_LOG);
    var wrappedReporter = mock(Reporter.class);
    var reporter = new TaskFilteringReporter(wrappedReporter, reportMatcher);

    reporter.report(label, details, DISK, logs);

    verify(wrappedReporter, times(matched ? 1 : 0)).report(label, details, DISK, logs);
  }
}
