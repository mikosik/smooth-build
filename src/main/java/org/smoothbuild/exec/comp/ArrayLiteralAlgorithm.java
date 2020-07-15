package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.arrayAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.LITERAL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

public class ArrayLiteralAlgorithm implements Algorithm {
  private final ConcreteArrayType arrayType;

  public ArrayLiteralAlgorithm(ConcreteArrayType arrayType) {
    this.arrayType = arrayType;
  }

  @Override
  public TaskKind kind() {
    return LITERAL;
  }

  @Override
  public Hash hash() {
    return arrayAlgorithmHash();
  }

  @Override
  public ConcreteType type() {
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
