package org.smoothbuild.virtualmachine.evaluate.task;

import static org.smoothbuild.virtualmachine.evaluate.task.Purity.PURE;

import java.util.Objects;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public abstract sealed class Task
    permits CombineTask, ConstTask, InvokeTask, OrderTask, PickTask, SelectTask {
  private final BExpr expr;
  private final BTrace trace;

  public Task(BExpr expr, BTrace trace) {
    this.expr = expr;
    this.trace = trace;
  }

  public BExpr expr() {
    return expr;
  }

  public BTrace trace() {
    return trace;
  }

  public BType outputType() {
    return expr.evaluationType();
  }

  public Purity purity(BTuple input) throws BytecodeException {
    return PURE;
  }

  public abstract Output run(BTuple input, Container container) throws BytecodeException;

  @Override
  public int hashCode() {
    return expr.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Task that
        && Objects.equals(this.getClass(), that.getClass())
        && Objects.equals(this.expr, that.expr())
        && Objects.equals(this.trace, that.trace);
  }
}
