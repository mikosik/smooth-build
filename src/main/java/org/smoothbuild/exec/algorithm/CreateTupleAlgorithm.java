package org.smoothbuild.exec.algorithm;

import static org.smoothbuild.exec.algorithm.AlgorithmHashes.tupleAlgorithmHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.db.object.spec.Spec;
import org.smoothbuild.db.object.spec.TupleSpec;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.plugin.NativeApi;

public class CreateTupleAlgorithm extends Algorithm {
  private final TupleSpec constructedType;

  public CreateTupleAlgorithm(TupleSpec constructedType) {
    this.constructedType = constructedType;
  }

  @Override
  public Hash hash() {
    return tupleAlgorithmHash(constructedType);
  }

  @Override
  public Spec outputSpec() {
    return constructedType;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Tuple tuple = nativeApi.factory().tuple(constructedType, input.objects());
    return new Output(tuple, nativeApi.messages());
  }
}
