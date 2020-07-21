package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.AlgorithmHashes.createArrayAlgorithmHash;
import static org.smoothbuild.exec.task.base.TaskKind.LITERAL;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.base.TaskKind;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.spec.ArraySpec;
import org.smoothbuild.record.spec.Spec;

public class CreateArrayAlgorithm implements Algorithm {
  private final ArraySpec arraySpec;

  public CreateArrayAlgorithm(ArraySpec arraySpec) {
    this.arraySpec = arraySpec;
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
  public Spec type() {
    return arraySpec;
  }

  @Override
  public Output run(Input input, NativeApi nativeApi) {
    Array array = nativeApi
        .factory()
        .arrayBuilder(arraySpec.elemSpec())
        .addAll(input.objects())
        .build();
    return new Output(array, nativeApi.messages());
  }
}
