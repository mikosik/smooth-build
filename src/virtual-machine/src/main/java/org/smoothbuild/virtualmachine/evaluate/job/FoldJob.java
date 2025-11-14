package org.smoothbuild.virtualmachine.evaluate.job;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.schedule.Output.successOutput;
import static org.smoothbuild.virtualmachine.VmConstants.VM_LABEL;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.schedule.Task2;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BFold.BSubExprs;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.BEvaluateTask.JobContext;

public final class FoldJob extends SchedulingJob {
  private final BFold fold;

  public FoldJob(JobContext jobContext, BFold fold, List<Job> environment, Trace trace) {
    super(jobContext, fold, environment, trace);
    this.fold = fold;
  }

  @Override
  public Promise<Maybe<BValue>> schedule() throws BytecodeException {
    var subExprs = fold.subExprs();
    var arrayArg = subExprs.array();
    var initialArg = subExprs.initial();
    var schedulingTask = newFoldSchedulingTask(subExprs);
    var arrayPromise = evaluate(arrayArg);
    var initialPromise = evaluate(initialArg);
    return scheduler().submit(schedulingTask, arrayPromise, initialPromise);
  }

  private Task2<BValue, BValue, BValue> newFoldSchedulingTask(BSubExprs subExprs) {
    return (arrayValue, initialValue) -> {
      var label = VM_LABEL.append(":scheduleFold");
      try {
        var array = ((BArray) arrayValue);
        var folderArg = subExprs.folder();
        BExpr result = initialValue;
        for (BValue element : array.elements(BValue.class)) {
          result =
              bytecodeFactory().call(folderArg, bytecodeFactory().combine(list(result, element)));
        }
        return successOutput(evaluate(result), label, trace());
      } catch (BytecodeException e) {
        return failedSchedulingOutput(label, trace(), e);
      }
    };
  }
}
