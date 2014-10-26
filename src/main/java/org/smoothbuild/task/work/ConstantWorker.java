package org.smoothbuild.task.work;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.hash.HashCode;

public class ConstantWorker<T extends Value> extends TaskWorker<T> {
  private final T value;

  public ConstantWorker(Type<T> type, T value, CodeLocation codeLocation) {
    super(constantWorkerHash(value), type, type.name(), true, false, codeLocation);
    this.value = checkNotNull(value);
  }

  private static HashCode constantWorkerHash(Value value) {
    return WorkerHashes.workerHash(ConstantWorker.class, value.hash());
  }

  @Override
  public TaskOutput<T> execute(TaskInput input, NativeApiImpl nativeApi) {
    return new TaskOutput<>(value);
  }
}
