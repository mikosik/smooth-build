package org.smoothbuild.vm.algorithm;

import static org.smoothbuild.vm.algorithm.AlgorithmHashes.combineAlgorithmHash;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.plugin.NativeApi;

public class CombineAlgorithm extends Algorithm {
  public CombineAlgorithm(TupleTB tupleT) {
    super(tupleT);
  }

  @Override
  public Hash hash() {
    return combineAlgorithmHash((TupleTB) outputT());
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    return new Output(input, nativeApi.messages());
  }
}
