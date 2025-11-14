package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.schedule.Output.successOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.virtualmachine.VmConstants;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask.JobContext;

public final class IfJob extends SchedulingJob {
  private final BIf if_;

  public IfJob(JobContext jobContext, BIf if_, List<Job> environment, Trace trace) {
    super(jobContext, if_, environment, trace);
    this.if_ = if_;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    var subExprs = if_.subExprs();
    var schedulingTask = (Task1<BValue, BValue>) (conditionValue) -> {
      var label = VmConstants.VM_LABEL.append(":scheduleIf");
      try {
        var condition = ((BBool) conditionValue).toJavaBoolean();
        return successOutput(
            evaluate(condition ? subExprs.then_() : subExprs.else_()), label, trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(label, trace(), e);
      }
    };
    var conditionPromise = evaluate(subExprs.condition());
    return scheduler().submit(schedulingTask, conditionPromise);
  }
}
