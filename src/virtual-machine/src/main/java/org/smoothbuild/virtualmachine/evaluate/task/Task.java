package org.smoothbuild.virtualmachine.evaluate.task;

import java.util.Objects;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public abstract sealed class Task
    permits CombineTask, ConstTask, InvokeTask, OrderTask, PickTask, SelectTask {
  private final BExpr expr;
  private final Purity purity;
  private final BTrace trace;

  public Task(BExpr expr, BTrace trace) {
    this(expr, trace, Purity.PURE);
  }

  public Task(BExpr expr, BTrace trace, Purity purity) {
    this.expr = expr;
    this.trace = trace;
    this.purity = purity;
  }

  public BExpr exprB() {
    return expr;
  }

  public BTrace trace() {
    return trace;
  }

  public BType outputType() {
    return expr.evaluationType();
  }

  public Purity purity() {
    return purity;
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
        && Objects.equals(this.expr, that.exprB())
        && Objects.equals(this.trace, that.trace);
  }
}
