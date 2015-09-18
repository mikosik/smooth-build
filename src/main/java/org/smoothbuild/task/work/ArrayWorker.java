package org.smoothbuild.task.work;

import static org.smoothbuild.task.work.WorkerHashes.workerHash;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.compute.ArrayAlgorithm;

import com.google.common.hash.HashCode;

public class ArrayWorker extends TaskWorker {
  private static final HashCode ARRAY_WORKER_HASH = workerHash(ArrayWorker.class);

  public ArrayWorker(ArrayType arrayType, CodeLocation codeLocation) {
    super(new ArrayAlgorithm(arrayType), ARRAY_WORKER_HASH, arrayType.name(), true, true,
        codeLocation);
  }
}
