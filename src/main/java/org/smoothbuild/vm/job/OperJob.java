package org.smoothbuild.vm.job;

import java.util.function.BiFunction;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.OperB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.task.Task;

public class OperJob extends ExecutingJob {
  private final BiFunction<TypeB, LabeledLoc, Task> taskCtor;
  private final OperB operB;

  public OperJob(BiFunction<TypeB, LabeledLoc, Task> taskCtor, OperB operB,
      ExecutionContext context) {
    super(context);
    this.taskCtor = taskCtor;
    this.operB = operB;
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var task = taskCtor.apply(operB.evalT(), context().labeledLoc(operB));
    return evaluateTransitively(task, operB.dataSeq());
  }
}
