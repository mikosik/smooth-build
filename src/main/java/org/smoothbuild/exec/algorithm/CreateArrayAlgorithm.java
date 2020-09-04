package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.arrayAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Array;
import org.smoothbuild.db.object.spec.ArraySpec;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CreateArrayAlgorithm implements Algorithm {
  private final ArraySpec arraySpec;

  public CreateArrayAlgorithm(ArraySpec arraySpec) {
    this.arraySpec = arraySpec;
  }

  @Override
  public Hash hash() {
    return arrayAlgorithmHash();
  }

  @Override
  public Spec outputSpec() {
    return arraySpec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Array array = nativeApi
        .factory()
        .arrayBuilder(arraySpec.elemSpec())
        .addAll(input.objects())
        .build();
    return new Output(array, nativeApi.messages());
  }
}
