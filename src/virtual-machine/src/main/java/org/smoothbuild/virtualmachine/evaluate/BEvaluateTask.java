package org.smoothbuild.virtualmachine.evaluate;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.successOutput;
import static org.smoothbuild.virtualmachine.VmConstants.VM_LABEL;

import jakarta.inject.Inject;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Output;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.dagger.PerVm;
import org.smoothbuild.virtualmachine.evaluate.job.JobContext;

/**
 * Evaluates BExpr.
 * This class is thread-safe.
 */
@PerVm
public class BEvaluateTask implements Task1<BExpr, BValue> {
  private final JobContext jobContext;

  @Inject
  public BEvaluateTask(JobContext jobContext) {
    this.jobContext = jobContext;
  }

  @Override
  public Output<BValue> execute(BExpr expr) {
    var label = VM_LABEL.append(":schedule");
    var job = jobContext.newJob(expr, list(), new Trace());
    return successOutput(job.evaluate(), label);
  }
}
