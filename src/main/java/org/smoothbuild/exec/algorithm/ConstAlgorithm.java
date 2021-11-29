package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.constAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class ConstAlgorithm extends Algorithm {
  private final ValueH val;

  public ConstAlgorithm(ValueH val) {
    super(val.spec());
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
