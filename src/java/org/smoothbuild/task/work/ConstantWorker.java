package org.smoothbuild.task.work;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.db.taskoutputs.TaskOutput;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.hash.HashCode;

public class ConstantWorker<T extends SValue> extends TaskWorker<T> {
  private final T value;

  public ConstantWorker(SType<T> type, T value, CodeLocation codeLocation) {
    super(workerHash(value), type, type.name(), true, false, codeLocation);
    this.value = checkNotNull(value);
  }

  private static HashCode workerHash(SValue value) {
    return WorkerHashes.workerHash(ConstantWorker.class, value.hash());
  }

  @Override
  public TaskOutput<T> execute(Iterable<? extends SValue> input, NativeApiImpl nativeApi) {
    return new TaskOutput<>(value);
  }
}
