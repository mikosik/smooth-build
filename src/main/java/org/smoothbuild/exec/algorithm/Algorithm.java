package org.smoothbuild.exec.algorithm;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public abstract class Algorithm {
  private final boolean isPure;

  protected Algorithm() {
    this.isPure = true;
  }

  protected Algorithm(boolean isPure) {
    this.isPure = isPure;
  }

  public boolean isPure() {
    return isPure;
  }

  public abstract Hash hash();

  public abstract Spec outputSpec();

  public abstract Output run(Input input, NativeApi nativeApi) throws Exception;
}
