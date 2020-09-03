package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.tupleAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.db.record.spec.TupleSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CreateTupleAlgorithm implements Algorithm {
  private final TupleSpec constructedType;

  public CreateTupleAlgorithm(TupleSpec constructedType) {
    this.constructedType = constructedType;
  }

  @Override
  public Hash hash() {
    return tupleAlgorithmHash(constructedType);
  }

  @Override
  public Spec type() {
    return constructedType;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Tuple tuple = nativeApi.factory().tuple(constructedType, input.records());
    return new Output(tuple, nativeApi.messages());
  }
}
