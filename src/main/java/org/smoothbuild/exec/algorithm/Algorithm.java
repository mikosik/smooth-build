package org.smoothbuild.exec.algorithm;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public abstract class Algorithm {
  private final boolean isPure;
  private final ValType outputTypes;

  protected Algorithm(ValType outputTypes) {
    this(outputTypes, true);
  }

  protected Algorithm(ValType outputTypes, boolean isPure) {
    this.outputTypes = outputTypes;
    this.isPure = isPure;
  }

  public ValType outputType() {
    return outputTypes;
  }

  public boolean isPure() {
    return isPure;
  }

  public abstract Hash hash();

  public abstract Output run(Input input, NativeApi nativeApi) throws Exception;
}
