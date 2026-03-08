package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.schedule.Output.successOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBool;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BIf;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BIfJob extends SchedulingJob {
  private final BIf if_;

  public BIfJob(JobContext jobContext, BIf if_, List<Job> environment, Trace trace) {
    super(jobContext, if_, environment, trace);
    this.if_ = if_;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    var schedulingTask = (Task1<BValue, BValue>) (conditionValue) -> {
      try {
        var condition = ((BBool) conditionValue).toJavaBoolean();
        return successOutput(
            evaluate(condition ? if_.then_() : if_.else_()), executeLabel(), trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
    var conditionPromise = evaluate(if_.condition());
    return scheduler().submit(schedulingTask, conditionPromise);
  }

  private Label executeLabel() {
    return scheduleLabel("execute");
  }
}
