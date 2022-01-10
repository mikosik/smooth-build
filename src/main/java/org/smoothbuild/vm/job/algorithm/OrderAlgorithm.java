package org.smoothbuild.vm.job.algorithm;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.plugin.NativeApi;

public class OrderAlgorithm extends Algorithm {
  public OrderAlgorithm(ArrayTB arrayT) {
    super(arrayT);
  }

  @Override
  public Hash hash() {
    return AlgorithmHashes.orderAlgorithmHash(outputT());
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