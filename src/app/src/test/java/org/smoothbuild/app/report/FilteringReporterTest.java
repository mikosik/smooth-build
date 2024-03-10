package org.smoothbuild.app.report;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.app.run.eval.report.TaskMatchers.ALL;
import static org.smoothbuild.app.run.eval.report.TaskMatchers.NONE;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.ResultSource.DISK;
import static org.smoothbuild.common.testing.TestingLog.ERROR_LOG;

import org.junit.jupiter.api.Test;
import org.smoothbuild.app.run.eval.report.TaskMatcher;
import org.smoothbuild.common.step.StepReporter;

public class FilteringReporterTest {
  @Test
  public void when_filter_matches() throws Exception {
    testVisibility(ALL, true);
  }

  @Test
  public void when_filter_not_matches() throws Exception {
    testVisibility(NONE, false);
  }

  private void testVisibility(TaskMatcher taskMatcher, boolean matched) throws Exception {
    var label = label("name");
    var details = "details";
    var logs = list(ERROR_LOG);
    var wrappedReporter = mock(StepReporter.class);
    var reporter = new FilteringReporter(wrappedReporter, taskMatcher);

    reporter.report(label, details, DISK, logs);

    if (matched) {
      verify(wrappedReporter, times(matched ? 1 : 0)).report(label, details, DISK, logs);
    }
  }
}
