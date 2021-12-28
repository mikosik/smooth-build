package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.orderAlgorithmHash;

import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.type.val.ArrayTB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class OrderAlgorithm extends Algorithm {
  public OrderAlgorithm(ArrayTB arrayT) {
    super(arrayT);
  }

  @Override
  public Hash hash() {
    return orderAlgorithmHash(outputT());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    ArrayB array = nativeApi
        .factory()
        .arrayBuilder((ArrayTB) outputT())
        .addAll(input.vals())
        .build();
    return new Output(array, nativeApi.messages());
  }
}
