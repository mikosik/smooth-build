package org.smoothbuild.vm.job;

import java.util.function.BiFunction;

import org.smoothbuild.bytecode.expr.oper.OperB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.execute.TraceB;
import org.smoothbuild.vm.task.Task;

public class OperJob<T extends OperB> extends Job {
  private final BiFunction<T, TraceB, Task> taskCreator;

  public OperJob(BiFunction<T, TraceB, Task> taskCreator, T operB, ExecutionContext context) {
    super(operB, context);
    this.taskCreator = taskCreator;
  }

  @Override
  @SuppressWarnings("unchecked")
  public T exprB() {
    return (T) super.exprB();
  }

  @Override
  protected Promise<ValueB> evaluateImpl() {
    var task = taskCreator.apply(exprB(), context().trace());
    return evaluateTransitively(task, exprB().dataSeq());
  }
}
