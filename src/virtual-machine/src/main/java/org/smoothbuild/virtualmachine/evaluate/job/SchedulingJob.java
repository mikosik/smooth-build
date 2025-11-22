package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.base.Throwables.messageFrom;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Output.output;
import static org.smoothbuild.common.schedule.Output.successOutput;
import static org.smoothbuild.virtualmachine.VmConstants.VM_LABEL;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task0;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public abstract sealed class SchedulingJob extends Job
    permits BCallJob, BFoldJob, BIfJob, BMapJob, BOperationJob, BReferenceJob, BSwitchJob {
  public SchedulingJob(
      JobContext jobContext, BOperation operation, List<Job> environment, Trace trace) {
    super(jobContext, operation, environment, trace);
  }

  @Override
  public Promise<Maybe<BValue>> evaluate() {
    Task0<BValue> t = () -> {
      try {
        var result = schedule();
        return successOutput(result, scheduleLabel());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(scheduleLabel(), trace(), e);
      } catch (JobException e) {
        return output(report(scheduleLabel(), trace(), list(fatal(messageFrom(e)))));
      }
    };
    return scheduler().submit(t);
  }

  protected Label scheduleLabel2(String name) {
    return scheduleLabel().append(":" + name);
  }

  private Label scheduleLabel() {
    return VM_LABEL.append(":schedule:" + ((BOperation) expr()).name());
  }

  public abstract Promise<Maybe<BValue>> schedule() throws JobException, BytecodeException;
}
