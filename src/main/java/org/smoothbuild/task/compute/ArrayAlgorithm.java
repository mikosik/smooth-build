package org.smoothbuild.task.compute;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

public class ArrayAlgorithm implements Algorithm {
  private final ArrayType arrayType;

  public ArrayAlgorithm(ArrayType arrayType) {
    this.arrayType = arrayType;
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    Class<? extends Value> elementClass = (Class<? extends Value>) arrayType.elemType().jType()
        .getRawType();
    return new TaskOutput(inputToArray(input, container, elementClass));
  }

  private <T extends Value> Array<T> inputToArray(TaskInput input, ContainerImpl container,
      Class<T> elementClass) {
    ArrayBuilder<T> builder = container.arrayBuilder(elementClass);
    for (T value : (Iterable<T>) input.values()) {
      builder.add(value);
    }
    return builder.build();
  }
}
