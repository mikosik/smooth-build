package org.smoothbuild.task.work;

import static org.smoothbuild.task.work.WorkerHashes.workerHash;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.hash.HashCode;

public class ArrayWorker<T extends Value> extends TaskWorker<Array<T>> {
  private static final HashCode ARRAY_WORKER_HASH = workerHash(ArrayWorker.class);

  private final ArrayType<T> arrayType;

  public ArrayWorker(ArrayType<T> arrayType, CodeLocation codeLocation) {
    super(ARRAY_WORKER_HASH, arrayType, arrayType.name(), true, true, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public TaskOutput execute(TaskInput input, NativeApiImpl nativeApi) {
    Class<T> elementClass = (Class<T>) arrayType.elemType().jType().getRawType();
    ArrayBuilder<T> builder = nativeApi.arrayBuilder(elementClass);
    for (T value : (Iterable<T>) input.values()) {
      builder.add(value);
    }
    Array<T> result = builder.build();
    return new TaskOutput(result);
  }
}
