package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.ComputationHashes.arrayComputationHash;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

public class ArrayComputation implements Computation {
  private final ArrayType arrayType;

  public ArrayComputation(ArrayType arrayType) {
    this.arrayType = arrayType;
  }

  @Override
  public HashCode hash() {
    return arrayComputationHash();
  }

  @Override
  public ConcreteType resultType() {
    return arrayType;
  }

  @Override
  public Output execute(Input input, Container container) {
    return new Output(container
        .create()
        .arrayBuilder(arrayType.elemType())
        .addAll(input.values())
        .build());
  }
}
