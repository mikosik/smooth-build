package org.smoothbuild.vm.task;

import static org.smoothbuild.vm.task.Purity.PURE;

import java.util.Objects;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.type.value.TypeB;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.TraceB;

public sealed abstract class Task
    permits CombineTask, ConstTask, InvokeTask, OrderTask, PickTask, SelectTask {
  private final ExprB exprB;
  private final Purity purity;
  private final TraceB trace;

  public Task(ExprB exprB, TraceB trace) {
    this(exprB, trace, PURE);
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
    return exprB.evalT();
  }

  public Purity purity() {
    return purity;
  }

  public abstract Output run(TupleB input, Container container);

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
