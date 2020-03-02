package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.ComputationHashes.arrayComputationHash;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.lang.plugin.NativeApi;

public class ArrayComputation implements Computation {
  private final ConcreteArrayType arrayType;

  public ArrayComputation(ConcreteArrayType arrayType) {
    this.arrayType = arrayType;
  }

  @Override
  public String name() {
    return arrayType.name();
  }

  @Override
  public Hash hash() {
    return arrayComputationHash();
  }

  @Override
  public ConcreteType type() {
    return arrayType;
  }

  @Override
  public Output execute(Input input, NativeApi nativeApi) {
    Array array = nativeApi
        .factory()
        .arrayBuilder(arrayType.elemType())
        .addAll(input.objects())
        .build();
    return new Output(array, emptyMessageArray(nativeApi));
  }
}
