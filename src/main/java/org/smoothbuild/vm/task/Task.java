package org.smoothbuild.vm.task;

import static org.smoothbuild.vm.task.Purity.PURE;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.TaskKind;
import org.smoothbuild.vm.execute.TraceB;

public sealed abstract class Task permits CombineTask, ConstTask, NativeCallTask,
    OrderTask, PickTask, SelectTask{
  private final ExprB exprB;
  private final Purity purity;
  private final TaskKind kind;
  private final TraceB trace;

  public Task(ExprB exprB, TaskKind kind, TraceB trace) {
    this(exprB, kind, trace, PURE);
  }

  public Task(ExprB exprB, TaskKind kind, TraceB trace, Purity purity) {
    this.exprB = exprB;
    this.kind = kind;
    this.trace = trace;
    this.purity = purity;
  }

  public ExprB exprB() {
    return exprB;
  }

  public TaskKind kind() {
    return kind;
  }

  public TypeB outputT() {
    return exprB.evalT();
  }

  public Purity purity() {
    return purity;
  }

  public abstract Output run(TupleB input, Container container);
}
