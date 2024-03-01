package org.smoothbuild.virtualmachine.evaluate.task;

import java.util.Objects;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.TraceB;

public abstract sealed class Task
    permits CombineTask, ConstTask, InvokeTask, OrderTask, PickTask, SelectTask {
  private final ExprB exprB;
  private final Purity purity;
  private final TraceB trace;

  public Task(ExprB exprB, TraceB trace) {
    this(exprB, trace, Purity.PURE);
  }

  public Task(ExprB exprB, TraceB trace, Purity purity) {
    this.exprB = exprB;
    this.trace = trace;
    this.purity = purity;
  }

  public ExprB exprB() {
    return exprB;
  }

  public TraceB trace() {
    return trace;
  }

  public TypeB outputT() {
    return exprB.evaluationType();
  }

  public Purity purity() {
    return purity;
  }

  public abstract Output run(TupleB input, Container container) throws BytecodeException;

  @Override
  public int hashCode() {
    return exprB.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Task that
        && Objects.equals(this.getClass(), that.getClass())
        && Objects.equals(this.exprB, that.exprB())
        && Objects.equals(this.trace, that.trace);
  }
}
