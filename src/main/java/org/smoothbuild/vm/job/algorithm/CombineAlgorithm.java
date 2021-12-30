package org.smoothbuild.vm.job.algorithm;

import static org.smoothbuild.vm.job.algorithm.AlgorithmHashes.combineAlgorithmHash;

import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.type.val.TupleTB;
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
  public Output run(Input input, NativeApi nativeApi) {
    TupleB tuple = nativeApi.factory().tuple(((TupleTB) outputT()), input.vals());
    return new Output(tuple, nativeApi.messages());
  }
}
