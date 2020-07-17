package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.createArrayAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.LITERAL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.type.ArrayType;
import org.smoothbuild.record.type.BinaryType;

public class CreateArrayAlgorithm implements Algorithm {
  private final ArrayType arrayType;

  public CreateArrayAlgorithm(ArrayType arrayType) {
    this.arrayType = arrayType;
  }

  @Override
  public TaskKind kind() {
    return LITERAL;
  }

  @Override
  public Hash hash() {
    return createArrayAlgorithmHash();
  }

  @Override
  public BinaryType type() {
    return arrayType;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Array array = nativeApi
        .factory()
        .arrayBuilder(arrayType.elemType())
        .addAll(input.objects())
        .build();
    return new Output(array, nativeApi.messages());
  }
}
