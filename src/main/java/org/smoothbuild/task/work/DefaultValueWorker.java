package org.smoothbuild.task.work;

import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.compute.ConstantAlgorithm;

import com.google.common.hash.HashCode;

public class DefaultValueWorker extends TaskWorker {
  public DefaultValueWorker(Type type, Value value, CodeLocation codeLocation) {
    super(new ConstantAlgorithm(value), defaultValueWorkerHash(value), type.name(), true, true,
        codeLocation);
  }

  private static HashCode defaultValueWorkerHash(Value value) {
    return WorkerHashes.workerHash(DefaultValueWorker.class, value.hash());
  }
}
