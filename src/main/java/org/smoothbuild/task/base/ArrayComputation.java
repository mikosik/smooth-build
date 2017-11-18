package org.smoothbuild.task.base;

import static org.smoothbuild.task.base.ComputationHashes.arrayComputationHash;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Value;
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
  public Type resultType() {
    return arrayType;
  }

  @Override
  public Output execute(Input input, Container container) {
    return new Output(inputToArray(input, container));
  }

  private Array inputToArray(Input input, Container container) {
    ArrayBuilder builder = container.create().arrayBuilder(arrayType.elemType());
    for (Value value : input.values()) {
      builder.add(value);
    }
    return builder.build();
  }
}
