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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BSwitchJob extends SchedulingJob {
  private final BSwitch switch_;

  public BSwitchJob(JobContext jobContext, BSwitch switch_, List<Job> environment, Trace trace) {
    super(jobContext, switch_, environment, trace);
    this.switch_ = switch_;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    var choicePromise = evaluate(switch_.choice());
    var schedulingTask = newSwitchSchedulingTask();
    return scheduler().submit(schedulingTask, choicePromise);
  }

  private Task1<BValue, BValue> newSwitchSchedulingTask() {
    return (choiceValue) -> {
      try {
        var choice = (BChoice) choiceValue;
        var index = choice.index().toJavaBigInteger();
        var handler = switch_.handlers().items().get(index.intValue());
        var call = call(handler, list(choice.chosen()));
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
