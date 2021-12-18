package org.smoothbuild.exec.algorithm;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
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
    checkArgument(!outputT.isPolytype());
  }

  public TypeB outputT() {
    return outputT;
  }

  public boolean isPure() {
    return isPure;
  }

  public abstract Hash hash();

  public abstract Output run(Input input, NativeApi nativeApi) throws Exception;
}
