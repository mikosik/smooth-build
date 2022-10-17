package org.smoothbuild.vm.job;

import java.util.function.BiFunction;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.oper.OperB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.execute.TraceB;
import org.smoothbuild.vm.task.Task;

public class OperJob<T extends OperB> extends Job {
  private final BiFunction<T, TraceB, Task> taskConstructor;
  private final T operB;

  public OperJob(BiFunction<T, TraceB, Task> taskConstructor, T operB, ExecutionContext context) {
    super(context);
    this.taskConstructor = taskConstructor;
    this.operB = operB;
  }

  @Override
  protected Promise<InstB> evaluateImpl() {
    var task = taskConstructor.apply(operB, context().trace());
    return evaluateTransitively(task, operB.dataSeq());
  }
}
