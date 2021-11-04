package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.createStructAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.type.val.StructTypeO;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CreateStructAlgorithm extends Algorithm {
  public CreateStructAlgorithm(StructTypeO structType) {
    super(structType);
  }

  @Override
  public Hash hash() {
    return createStructAlgorithmHash((StructTypeO) outputType());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Struc_ struct = nativeApi.factory().struct(((StructTypeO) outputType()), input.vals());
    return new Output(struct, nativeApi.messages());
  }
}
