package org.smoothbuild.exec.comp;

import static org.smoothbuild.exec.comp.ComputationHashes.arrayComputationHash;
import static org.smoothbuild.lang.object.base.Messages.emptyMessageArray;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.task.Container;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.type.ConcreteArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;

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
        .factory()
        .arrayBuilder(arrayType.elemType())
        .addAll(input.objects())
        .build();
    return new Output(array, emptyMessageArray(container));
  }
}
