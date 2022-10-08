package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.execute.TaskKind.COMBINE;
import static org.smoothbuild.vm.task.TaskHashes.combineTaskHash;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.inst.TupleTB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.plugin.NativeApi;

public class CombineTask extends Task {
  public CombineTask(TypeB tupleT, LabeledLoc labeledLoc) {
    super(tupleT, COMBINE, labeledLoc);
    checkArgument(tupleT instanceof TupleTB);
  }

  @Override
  public Hash hash() {
    return combineTaskHash(outputT());
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    return new Output(input, nativeApi.messages());
  }
}
