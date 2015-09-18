package org.smoothbuild.task.work;

import static org.smoothbuild.task.work.WorkerHashes.workerHash;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.compute.Algorithm;
import org.smoothbuild.task.compute.ArrayAlgorithm;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class ArrayWorker extends TaskWorker {
  private static final HashCode ARRAY_WORKER_HASH = workerHash(ArrayWorker.class);

  private final Algorithm algorithm;

  public ArrayWorker(ArrayType arrayType, CodeLocation codeLocation) {
    super(ARRAY_WORKER_HASH, arrayType, arrayType.name(), true, true, codeLocation);
    this.algorithm = new ArrayAlgorithm(arrayType);
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return algorithm.execute(input, container);
  }
}
