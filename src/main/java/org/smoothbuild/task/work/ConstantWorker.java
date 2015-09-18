package org.smoothbuild.task.work;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.compute.Algorithm;
import org.smoothbuild.task.compute.ConstantAlgorithm;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class ConstantWorker extends TaskWorker {
  private final Algorithm algorithm;

  public ConstantWorker(Type type, Value value, CodeLocation codeLocation) {
    super(constantWorkerHash(value), type, type.name(), true, false, codeLocation);
    this.algorithm = new ConstantAlgorithm(value);
  }

  private static HashCode constantWorkerHash(Value value) {
    return WorkerHashes.workerHash(ConstantWorker.class, value.hash());
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return algorithm.execute(input, container);
  }
}
