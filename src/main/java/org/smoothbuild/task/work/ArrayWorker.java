package org.smoothbuild.task.work;

import static org.smoothbuild.task.work.WorkerHashes.workerHash;

import org.smoothbuild.lang.base.ArrayBuilder;
import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.hash.HashCode;

public class ArrayWorker<T extends SValue> extends TaskWorker<SArray<T>> {
  private static final HashCode ARRAY_WORKER_HASH = workerHash(ArrayWorker.class);

  private final SArrayType<T> arrayType;

  public ArrayWorker(SArrayType<T> arrayType, CodeLocation codeLocation) {
    super(ARRAY_WORKER_HASH, arrayType, arrayType.name(), true, true, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public TaskOutput<SArray<T>> execute(TaskInput input, NativeApiImpl nativeApi) {
    @SuppressWarnings("unchecked")
    Iterable<T> castInput = (Iterable<T>) input.values();
    ArrayBuilder<T> builder = nativeApi.arrayBuilder(arrayType);
    for (T value : castInput) {
      builder.add(value);
    }
    SArray<T> result = builder.build();
    return new TaskOutput<>(result);
  }
}
