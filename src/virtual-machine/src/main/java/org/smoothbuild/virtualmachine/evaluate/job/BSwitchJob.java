package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.successOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoice;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch.BSubExprs;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BSwitchJob extends SchedulingJob {
  private final BSwitch switch_;

  public BSwitchJob(JobContext jobContext, BSwitch switch_, List<Job> environment, Trace trace) {
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
      try {
        var members = ((BChoice) choiceValue).components();
        var index = members.index().toJavaBigInteger();
        var handler = subExprs.handlers().items().get(index.intValue());
        var call = call(handler, list(members.chosen()));
        var result = evaluate(call);
        return successOutput(result, executeLabel(), trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
  }

  private Label executeLabel() {
    return scheduleLabel2("execute");
  }
}
