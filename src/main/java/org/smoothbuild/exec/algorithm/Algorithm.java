package org.smoothbuild.exec.algorithm;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public abstract class Algorithm {
  private final boolean isPure;
  private final TypeHV outputType;

  protected Algorithm(TypeHV outputType) {
    this(outputType, true);
  }

  protected Algorithm(TypeHV outputType, boolean isPure) {
    this.outputType = outputType;
    this.isPure = isPure;
  }

  public TypeHV outputType() {
    return outputType;
  }

  public boolean isPure() {
    return isPure;
  }

  public abstract Hash hash();

  public abstract Output run(Input input, NativeApi nativeApi) throws Exception;
}
