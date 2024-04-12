package org.smoothbuild.evaluator;

import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReport;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;

public class TaskReporterImpl implements TaskReporter {
  private final Reporter reporter;
  private final BsTranslator bsTranslator;

  public TaskReporterImpl(Reporter reporter, BsTranslator bsTranslator) {
    this.reporter = reporter;
    this.bsTranslator = bsTranslator;
  }

  @Override
  public void report(TaskReport taskReport) {
    var traceS = bsTranslator.translate(taskReport.trace());
    var details = traceS == null ? "" : traceS.toString();
    reporter.report(
        Report.report(taskReport.label(), details, taskReport.source(), taskReport.logs()));
  }
}
