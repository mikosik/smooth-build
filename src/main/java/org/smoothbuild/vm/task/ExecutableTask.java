package org.smoothbuild.vm.task;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.execute.TaskKind;

public abstract class ExecutableTask extends Task {
  private final Hash hash;

  protected ExecutableTask(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace,
      Hash hash) {
    this(outputT, kind, tagLoc, trace, true, hash);
  }

  protected ExecutableTask(TypeB outputT, TaskKind kind, TagLoc tagLoc, TraceS trace,
      boolean isPure, Hash hash) {
    super(outputT, kind, tagLoc, trace, isPure);
    this.hash = hash;
  }

  public Hash hash() {
    return hash;
  }

  public abstract Output run(TupleB input, NativeApi nativeApi);
}
