package org.smoothbuild.vm.execute;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.out.log.Log.error;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.out.log.Log.info;
import static org.smoothbuild.out.log.Log.warning;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.vm.report.TaskMatchers.ALL;
import static org.smoothbuild.vm.report.TaskMatchers.NONE;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.sb.BsMapping;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.report.TaskMatcher;

public class TaskReporterTest extends TestContext {
  @Test
  public void when_filter_matches() {
    testVisibility(ALL, true);
  }

  @Test
  public void when_filter_doesnt_match() {
    testVisibility(NONE, false);
  }

  private void testVisibility(TaskMatcher taskMatcher, boolean visible) {
    var reporter = mock(Reporter.class);
    var taskReporter = new TaskReporter(taskMatcher, reporter, new BsMapping());
    var messages = arrayB(
        fatalMessage(),
        errorMessage(),
        warningMessage(),
        infoMessage());
    taskReporter.report(task(), computationResultWithMessages(messages));
    var header = "[]                                          unknown                        exec";
    var logs = list(
        fatal("fatal message"),
        error("error message"),
        warning("warning message"),
        info("info message"));
    verify(reporter)
        .report(visible, header, logs);
  }
}
