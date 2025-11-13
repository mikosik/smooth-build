package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.schedule.Output.successOutput;
import static org.smoothbuild.virtualmachine.VmConstants.VM_LABEL;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public abstract sealed class SchedulingJob extends Job
    permits CallJob, FoldJob, IfJob, MapJob, OperationJob, ReferenceJob, SwitchJob {
  public SchedulingJob(
      JobContext jobContext, BOperation operation, List<Job> environment, Trace trace) {
    super(jobContext, operation, environment, trace);
  }

  @Override
  public Promise<Maybe<BValue>> evaluate() {
    Task0<BValue> t = () -> {
      var label = VM_LABEL.append(":schedule:" + ((BOperation) expr()).name());
      try {
        var result = schedule();
        return successOutput(result, label);
      } catch (BytecodeException e) {
        return failedSchedulingOutput(label, trace(), e);
      }
    };
    return scheduler().submit(t);
  }

  public abstract Promise<Maybe<BValue>> schedule() throws BytecodeException;
}
