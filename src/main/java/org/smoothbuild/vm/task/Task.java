package org.smoothbuild.vm.task;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.execute.TaskInfo;
import org.smoothbuild.vm.execute.TaskKind;

public abstract class Task {
  private final TypeB outputT;
  private final TaskInfo info;
  private final boolean isPure;

  protected Task(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace) {
    this(outputT, kind, tagLoc, trace, true);
  }

  protected Task(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace, boolean isPure) {
    this.outputT = outputT;
    this.info = new TaskInfo(kind, tagLoc, trace);
    this.isPure = isPure;
  }

  public TypeB outputT() {
    return outputT;
  }

  public TaskInfo info() {
    return info;
  }

  public boolean isPure() {
    return isPure;
  }

  public abstract Hash hash();

  public abstract Output run(TupleB input, NativeApi nativeApi) throws Exception;
}
