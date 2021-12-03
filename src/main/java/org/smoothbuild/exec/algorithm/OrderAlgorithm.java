package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.orderAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.type.val.ArrayTH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class OrderAlgorithm extends Algorithm {
  public OrderAlgorithm(ArrayTH arrayType) {
    super(arrayType);
  }

  @Override
  public Hash hash() {
    return orderAlgorithmHash();
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ArrayH array = nativeApi
        .factory()
        .arrayBuilder((ArrayTH) outputType())
        .addAll(input.vals())
        .build();
    return new Output(array, nativeApi.messages());
  }
}
