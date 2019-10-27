package org.smoothbuild.task.base;

import static org.smoothbuild.lang.message.Messages.emptyMessageArray;
import static org.smoothbuild.task.base.ComputationHashes.arrayComputationHash;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.task.exec.Container;

public class ArrayComputation implements Computation {
  private final ConcreteArrayType arrayType;

  public ArrayComputation(ConcreteArrayType arrayType) {
    this.arrayType = arrayType;
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
  public Output execute(Input input, Container container) {
    Array array = container
        .create()
        .arrayBuilder(arrayType.elemType())
        .addAll(input.values())
        .build();
    return new Output(array, emptyMessageArray(container));
  }
}
