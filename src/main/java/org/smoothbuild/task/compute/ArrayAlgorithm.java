package org.smoothbuild.task.compute;

import static org.smoothbuild.task.compute.AlgorithmHashes.arrayAlgorithmHash;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Output;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class ArrayAlgorithm implements Algorithm {
  private final ArrayType arrayType;

  public ArrayAlgorithm(ArrayType arrayType) {
    this.arrayType = arrayType;
  }

  @Override
  public HashCode hash() {
    return arrayAlgorithmHash();
  }

  @Override
  public Type resultType() {
    return arrayType;
  }

  @Override
  public Output execute(Input input, ContainerImpl container) {
    Class<? extends Value> elementClass = (Class<? extends Value>) arrayType.elemType().jType()
        .getRawType();
    return new Output(inputToArray(input, container, elementClass));
  }

  private <T extends Value> Array<T> inputToArray(Input input, ContainerImpl container,
      Class<T> elementClass) {
    ArrayBuilder<T> builder = container.arrayBuilder(elementClass);
    for (T value : (Iterable<T>) input.values()) {
      builder.add(value);
    }
    return builder.build();
  }
}
