package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.combineAlgorithmHash;

import org.smoothbuild.db.bytecode.obj.val.TupleB;
import org.smoothbuild.db.bytecode.type.val.TupleTB;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
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
