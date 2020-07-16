package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.createTupleAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.CALL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.object.base.Tuple;
import org.smoothbuild.lang.object.type.BinaryType;
import org.smoothbuild.lang.object.type.TupleType;
import org.smoothbuild.lang.plugin.NativeApi;

public class CreateTupleAlgorithm implements Algorithm {
  private final TupleType constructedType;

  public CreateTupleAlgorithm(TupleType constructedType) {
    this.constructedType = constructedType;
  }

  @Override
  public Hash hash() {
    return createTupleAlgorithmHash(constructedType);
  }

  @Override
  public BinaryType type() {
    return constructedType;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Tuple tuple = nativeApi.factory().struct(constructedType, input.objects());
    return new Output(tuple, nativeApi.messages());
  }

  @Override
  public TaskKind kind() {
    return CALL;
  }
}
