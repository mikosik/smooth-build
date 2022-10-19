package org.smoothbuild.vm.task;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.execute.TaskKind;

public sealed abstract class ExecutableTask extends Task permits CombineTask, ConstTask,
    IdentityTask, NativeCallTask, OrderTask, PickTask, SelectTask {
  protected ExecutableTask(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace) {
    this(outputT, kind, tagLoc, trace, true);
  }

  protected ExecutableTask(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace,
      boolean isPure) {
    super(outputT, kind, tagLoc, trace, isPure);
  }

  public abstract Output run(TupleB input, NativeApi nativeApi);
}
