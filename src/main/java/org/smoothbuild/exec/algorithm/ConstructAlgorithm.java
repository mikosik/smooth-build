package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.constructAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.val.TupleTypeH;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class ConstructAlgorithm extends Algorithm {
  public ConstructAlgorithm(TupleTypeH tupleType) {
    super(tupleType);
  }

  @Override
  public Hash hash() {
    return constructAlgorithmHash((TupleTypeH) outputType());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    TupleH tuple = nativeApi.factory().tuple(((TupleTypeH) outputType()), input.vals());
    return new Output(tuple, nativeApi.messages());
  }
}
