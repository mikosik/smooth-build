package org.smoothbuild.evaluator;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.NOOP;
import static org.smoothbuild.virtualmachine.VirtualMachineConstants.EVALUATE_PREFIX;

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

  @Override
  public void reportEvaluationException(Throwable throwable) {
    reporter.report(Report.report(
        label(EVALUATE_PREFIX),
        "",
        NOOP,
        list(fatal("Evaluation failed with: " + getStackTraceAsString(throwable)))));
  }
}
