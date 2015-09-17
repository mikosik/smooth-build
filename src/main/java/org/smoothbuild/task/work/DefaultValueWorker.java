package org.smoothbuild.task.work;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class DefaultValueWorker extends TaskWorker {
  private final Value value;

  public DefaultValueWorker(Type type, Value value, CodeLocation codeLocation) {
    super(defaultValueWorkerHash(value), type, type.name(), true, true, codeLocation);
    this.value = checkNotNull(value);
  }

  private static HashCode defaultValueWorkerHash(Value value) {
    return WorkerHashes.workerHash(DefaultValueWorker.class, value.hash());
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return new TaskOutput(value);
  }
}
