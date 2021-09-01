package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.tupleAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.val.Tuple;
import org.smoothbuild.db.object.spec.val.TupleSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CreateTupleAlgorithm extends Algorithm {
  public CreateTupleAlgorithm(TupleSpec tupleSpec) {
    super(tupleSpec);
  }

  @Override
  public Hash hash() {
    return tupleAlgorithmHash(outputSpec());
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Tuple tuple = nativeApi.factory().tuple(((TupleSpec) outputSpec()), input.objects());
    return new Output(tuple, nativeApi.messages());
  }
}
