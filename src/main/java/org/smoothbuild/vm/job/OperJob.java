package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.OperB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.base.Trace;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.function.TriFunction;
import org.smoothbuild.vm.task.Task;

public class OperJob extends ExecutingJob {
  private final TriFunction<TypeB, TagLoc, Trace, Task> taskConstructor;
  private final OperB operB;

  public OperJob(TriFunction<TypeB, TagLoc, Trace, Task> taskConstructor, OperB operB,
      ExecutionContext context) {
    super(context);
    this.taskConstructor = taskConstructor;
    this.operB = operB;
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var task = taskConstructor.apply(operB.evalT(), context().tagLoc(operB), context().trace());
    return evaluateTransitively(task, operB.dataSeq());
  }
}
