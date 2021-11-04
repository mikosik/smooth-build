package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.arrayAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.type.val.ArrayTypeO;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CreateArrayAlgorithm extends Algorithm {
  public CreateArrayAlgorithm(ArrayTypeO arrayType) {
    super(arrayType);
  }

  @Override
  public Hash hash() {
    return arrayAlgorithmHash();
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Array array = nativeApi
        .factory()
        .arrayBuilder(((ArrayTypeO) outputType()).element())
        .addAll(input.vals())
        .build();
    return new Output(array, nativeApi.messages());
  }
}
