package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.common.log.base.Log.fatal;

import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReport;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;

public class ForwardingTaskReporter implements TaskReporter {
  private final Reporter reporter;

  public ForwardingTaskReporter(Reporter reporter) {
    this.reporter = reporter;
  }

  @Override
  public void report(TaskReport taskReport) {
    reporter.report(Report.report(
        taskReport.label(), taskReport.trace().toString(), taskReport.source(), taskReport.logs()));
  }
}
