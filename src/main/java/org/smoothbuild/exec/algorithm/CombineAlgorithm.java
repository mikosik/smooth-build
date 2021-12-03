package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.combineAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.val.TupleTH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CombineAlgorithm extends Algorithm {
  public CombineAlgorithm(TupleTH tupleType) {
    super(tupleType);
  }

  @Override
  public Hash hash() {
    return combineAlgorithmHash((TupleTH) outputType());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    TupleH tuple = nativeApi.factory().tuple(((TupleTH) outputType()), input.vals());
    return new Output(tuple, nativeApi.messages());
  }
}
