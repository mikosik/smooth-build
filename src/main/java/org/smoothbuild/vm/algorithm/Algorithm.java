package org.smoothbuild.vm.algorithm;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.plugin.NativeApi;

public abstract class Algorithm {
  private final boolean isPure;
  private final TypeB outputT;

  protected Algorithm(TypeB outputT) {
    this(outputT, true);
  }

  protected Algorithm(TypeB outputT, boolean isPure) {
    this.outputT = outputT;
    this.isPure = isPure;
    checkArgument(outputT.vars().isEmpty());
  }

  public TypeB outputT() {
    return outputT;
  }

  public boolean isPure() {
    return isPure;
  }

  public abstract Hash hash();

  public abstract Output run(TupleB input, NativeApi nativeApi) throws Exception;
}
