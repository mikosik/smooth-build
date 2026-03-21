package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.successOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class BFoldJob extends SchedulingJob {
  private final BFold fold;

  public BFoldJob(JobContext jobContext, BFold fold, List<Job> environment, Trace trace) {
    super(jobContext, fold, environment, trace);
    this.fold = fold;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    var arrayArg = fold.array();
    var initialArg = fold.initial();
    var schedulingTask = newFoldSchedulingTask();
    var arrayPromise = evaluate(arrayArg);
    var initialPromise = evaluate(initialArg);
    return scheduler().submit(schedulingTask, arrayPromise, initialPromise);
  }

  private Task2<BValue, BValue, BValue> newFoldSchedulingTask() {
    return (arrayValue, initialValue) -> {
      try {
        var array = ((BArray) arrayValue);
        var folderArg = fold.folder();
        BExpr result = initialValue;
        for (BValue element : array.elements(BValue.class)) {
          result = bytecodeFactory()
              .call(folderArg, bytecodeFactory().constructTuple(list(result, element)));
        }
        return successOutput(evaluate(result), executeLabel(), trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(executeLabel(), trace(), e);
      }
    };
  }

  private Label executeLabel() {
    return scheduleLabel("execute");
  }
}
