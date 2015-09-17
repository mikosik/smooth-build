package org.smoothbuild.task.work;

import static org.smoothbuild.task.work.WorkerHashes.workerHash;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class ArrayWorker extends TaskWorker {
  private static final HashCode ARRAY_WORKER_HASH = workerHash(ArrayWorker.class);

  private final ArrayType arrayType;

  public ArrayWorker(ArrayType arrayType, CodeLocation codeLocation) {
    super(ARRAY_WORKER_HASH, arrayType, arrayType.name(), true, true, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    Class<? extends Value> elementClass =
        (Class<? extends Value>) arrayType.elemType().jType().getRawType();
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
