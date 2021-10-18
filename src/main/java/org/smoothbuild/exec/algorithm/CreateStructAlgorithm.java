package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.createStructAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.spec.val.StructSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CreateStructAlgorithm extends Algorithm {
  public CreateStructAlgorithm(StructSpec structSpec) {
    super(structSpec);
  }

  @Override
  public Hash hash() {
    return createStructAlgorithmHash((StructSpec) outputSpec());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Struc_ struct = nativeApi.factory().struct(((StructSpec) outputSpec()), input.vals());
    return new Output(struct, nativeApi.messages());
  }
}
