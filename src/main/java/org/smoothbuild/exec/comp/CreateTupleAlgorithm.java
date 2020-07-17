package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.createTupleAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.spec.Spec;
import org.smoothbuild.record.spec.TupleSpec;

public class CreateTupleAlgorithm implements Algorithm {
  private final TupleSpec constructedType;

  public CreateTupleAlgorithm(TupleSpec constructedType) {
    this.constructedType = constructedType;
  }

  @Override
  public Hash hash() {
    return createTupleAlgorithmHash(constructedType);
  }

  @Override
  public Spec type() {
    return constructedType;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Tuple tuple = nativeApi.factory().tuple(constructedType, input.objects());
    return new Output(tuple, nativeApi.messages());
  }

  @Override
  public TaskKind kind() {
    return CALL;
  }
}
