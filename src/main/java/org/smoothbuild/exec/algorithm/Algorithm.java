package org.smoothbuild.exec.algorithm;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public abstract class Algorithm {
  private final boolean isPure;
  private final Spec outputSpec;

  protected Algorithm(Spec outputSpec) {
    this(outputSpec, true);
  }

  protected Algorithm(Spec outputSpec, boolean isPure) {
    this.outputSpec = outputSpec;
    this.isPure = isPure;
  }

  public Spec outputSpec() {
    return outputSpec;
  }

  public boolean isPure() {
    return isPure;
  }

  public abstract Hash hash();

  public abstract Output run(Input input, NativeApi nativeApi) throws Exception;
}
