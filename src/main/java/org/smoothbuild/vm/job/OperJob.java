package org.smoothbuild.vm.job;

import java.util.function.BiFunction;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.OperB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.task.Task;

public class OperJob extends ExecutingJob {
  private final BiFunction<TypeB, TagLoc, Task> taskConstructor;
  private final OperB operB;

  public OperJob(BiFunction<TypeB, TagLoc, Task> taskConstructor, OperB operB,
      ExecutionContext context) {
    super(context);
    this.taskConstructor = taskConstructor;
    this.operB = operB;
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var task = taskConstructor.apply(operB.evalT(), context().tagLoc(operB));
    return evaluateTransitively(task, operB.dataSeq());
  }
}
