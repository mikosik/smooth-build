package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.constructAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.type.val.TupleTypeO;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class ConstructAlgorithm extends Algorithm {
  public ConstructAlgorithm(TupleTypeO tupleType) {
    super(tupleType);
  }

  @Override
  public Hash hash() {
    return constructAlgorithmHash((TupleTypeO) outputType());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Tuple tuple = nativeApi.factory().tuple(((TupleTypeO) outputType()), input.vals());
    return new Output(tuple, nativeApi.messages());
  }
}
