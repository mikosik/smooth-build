package org.smoothbuild.vm.task;

import static org.smoothbuild.vm.task.Purity.PURE;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.execute.TaskKind;

public sealed abstract class Task permits CombineTask, ConstTask, IdentityTask, NativeCallTask,
    OrderTask, PickTask, SelectTask{
  private final TypeB outputT;
  private final Purity purity;
  private final TaskKind kind;
  private final TagLoc tagLoc;
  private final TraceS trace;

  public Task(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace) {
    this(outputT, kind, tagLoc, trace, PURE);
  }

  public Task(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace, Purity purity) {
    this.outputT = outputT;
    this.kind = kind;
    this.tagLoc = tagLoc;
    this.trace = trace;
    this.purity = purity;
  }

  public TaskKind kind() {
    return kind;
  }

  public String tag() {
    return tagLoc.tag();
  }

  public Loc loc() {
    return tagLoc.loc();
  }

  public TypeB outputT() {
    return outputT;
  }

  public Purity purity() {
    return purity;
  }

  public abstract Output run(TupleB input, NativeApi nativeApi);
}
