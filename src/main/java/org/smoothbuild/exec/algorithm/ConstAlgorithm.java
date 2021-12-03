package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.constAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class ConstAlgorithm extends Algorithm {
  private final ValH val;

  public ConstAlgorithm(ValH val) {
    super(val.cat());
    this.val = val;
  }

  @Override
  public Hash hash() {
    return constAlgorithmHash(val);
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    return new Output(val, nativeApi.messages());
  }
}
