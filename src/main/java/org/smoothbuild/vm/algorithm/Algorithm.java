package org.smoothbuild.vm.algorithm;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;
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
