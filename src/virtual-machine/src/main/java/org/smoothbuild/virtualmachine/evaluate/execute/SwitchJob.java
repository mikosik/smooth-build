package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.successOutput;
import static org.smoothbuild.virtualmachine.VmConstants.VM_LABEL;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoice;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch.BSubExprs;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.execute.BEvaluate.JobContext;

public final class SwitchJob extends SchedulingJob {
  private final BSwitch switch_;

  public SwitchJob(JobContext jobContext, BSwitch switch_, List<Job> environment, Trace trace) {
    super(jobContext, switch_, environment, trace);
    this.switch_ = switch_;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    var subExprs = switch_.subExprs();
    var choicePromise = evaluate(subExprs.choice());
    var schedulingTask = newSwitchSchedulingTask(subExprs);
    return scheduler().submit(schedulingTask, choicePromise);
  }

  private Task1<BValue, BValue> newSwitchSchedulingTask(BSubExprs subExprs) {
    return (choiceValue) -> {
      var label = VM_LABEL.append(":scheduleChoice");
      try {
        var members = ((BChoice) choiceValue).members();
        var index = members.index().toJavaBigInteger();
        var handler = subExprs.handlers().items().get(index.intValue());
        var call = call(handler, list(members.chosen()));
        var result = evaluate(call);
        return successOutput(result, label, trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(label, trace(), e);
      }
    };
  }
}
