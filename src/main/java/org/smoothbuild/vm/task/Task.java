package org.smoothbuild.vm.task;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.execute.TaskInfo;
import org.smoothbuild.vm.execute.TaskKind;

public abstract class Task {
  private final TypeB outputT;
  private final TaskInfo info;
  private final boolean isPure;

  protected Task(TypeB outputT, TaskKind kind, ExprInfo exprInfo) {
    this(outputT, kind, exprInfo, true);
  }

  protected Task(TypeB outputT, TaskKind kind, ExprInfo exprInfo, boolean isPure) {
    this.outputT = outputT;
    this.info = new TaskInfo(kind, exprInfo);
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
