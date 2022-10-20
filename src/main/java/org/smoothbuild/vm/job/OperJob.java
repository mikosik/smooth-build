package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.OperB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.function.TriFunction;
import org.smoothbuild.vm.task.Task;

public class OperJob extends Job {
  private final TriFunction<TypeB, TagLoc, TraceS, Task> taskConstructor;
  private final OperB operB;

  public OperJob(TriFunction<TypeB, TagLoc, TraceS, Task> taskConstructor, OperB operB,
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
