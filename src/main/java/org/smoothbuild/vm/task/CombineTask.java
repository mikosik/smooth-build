package org.smoothbuild.vm.task;

import static org.smoothbuild.vm.execute.TaskKind.COMBINE;
import static org.smoothbuild.vm.task.TaskHashes.combineTaskHash;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.plugin.NativeApi;

public class CombineTask extends Task {
  public CombineTask(TupleTB tupleT, ExprInfo exprInfo) {
    super(tupleT, COMBINE, exprInfo);
  }

  @Override
  public Hash hash() {
    return combineTaskHash((TupleTB) outputT());
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    return new Output(input, nativeApi.messages());
  }
}
