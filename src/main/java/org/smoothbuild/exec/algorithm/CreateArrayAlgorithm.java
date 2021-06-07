package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.arrayAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CreateArrayAlgorithm extends Algorithm {
  public CreateArrayAlgorithm(ArraySpec arraySpec) {
    super(arraySpec);
  }

  @Override
  public Hash hash() {
    return arrayAlgorithmHash();
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Array array = nativeApi
        .factory()
        .arrayBuilder(((ArraySpec) outputSpec()).elemSpec())
        .addAll(input.objects())
        .build();
    return new Output(array, nativeApi.messages());
  }
}
