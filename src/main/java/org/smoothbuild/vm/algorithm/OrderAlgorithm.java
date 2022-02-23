package org.smoothbuild.vm.algorithm;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.TupleB;
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
  public Output run(TupleB input, NativeApi nativeApi) {
    ArrayB array = nativeApi
        .factory()
        .arrayBuilder((ArrayTB) outputT())
        .addAll(input.items())
        .build();
    return new Output(array, nativeApi.messages());
  }
}
