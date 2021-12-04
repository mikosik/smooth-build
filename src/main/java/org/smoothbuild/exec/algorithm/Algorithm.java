package org.smoothbuild.exec.algorithm;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public abstract class Algorithm {
  private final boolean isPure;
  private final TypeH outputT;

  protected Algorithm(TypeH outputT) {
    this(outputT, true);
  }

  protected Algorithm(TypeH outputT, boolean isPure) {
    this.outputT = outputT;
    this.isPure = isPure;
  }

  public TypeH outputT() {
    return outputT;
  }

  public boolean isPure() {
    return isPure;
  }

  public abstract Hash hash();

  public abstract Output run(Input input, NativeApi nativeApi) throws Exception;
}
