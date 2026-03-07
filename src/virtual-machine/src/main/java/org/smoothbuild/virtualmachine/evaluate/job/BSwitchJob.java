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
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSwitch;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BVariant;

public final class BSwitchJob extends SchedulingJob {
  private final BSwitch switch_;

  public BSwitchJob(JobContext jobContext, BSwitch switch_, List<Job> environment, Trace trace) {
    super(jobContext, switch_, environment, trace);
    this.switch_ = switch_;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    var variantPromise = evaluate(switch_.variant());
    var schedulingTask = newSwitchSchedulingTask();
    return scheduler().submit(schedulingTask, variantPromise);
  }

  private Task1<BValue, BValue> newSwitchSchedulingTask() {
    return (variantValue) -> {
      try {
        var variant = (BVariant) variantValue;
        var index = variant.index().toJavaBigInteger();
        var handler = switch_.handlers().items().get(index.intValue());
        var call = call(handler, list(variant.choice()));
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
