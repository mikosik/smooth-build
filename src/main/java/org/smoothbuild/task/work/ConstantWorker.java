package org.smoothbuild.task.work;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.compute.ConstantAlgorithm;

import com.google.common.hash.HashCode;

public class ConstantWorker extends TaskWorker {
  public ConstantWorker(Type type, Value value, CodeLocation codeLocation) {
    super(new ConstantAlgorithm(value), constantWorkerHash(value), type.name(), true, false,
        codeLocation);
  }

  private static HashCode constantWorkerHash(Value value) {
    return WorkerHashes.workerHash(ConstantWorker.class, value.hash());
  }
}
