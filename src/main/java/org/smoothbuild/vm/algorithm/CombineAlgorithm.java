package org.smoothbuild.vm.algorithm;

import static org.smoothbuild.vm.algorithm.AlgorithmHashes.combineAlgorithmHash;

import org.smoothbuild.bytecode.obj.cnst.TupleB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.db.Hash;
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
