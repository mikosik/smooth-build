package org.smoothbuild.task.work;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.compute.NativeCallAlgorithm;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class NativeCallWorker extends TaskWorker {
  private final NativeCallAlgorithm algorithm;

  public NativeCallWorker(NativeFunction function, boolean isInternal, CodeLocation codeLocation) {
    super(nativeCallWorkerHash(function), function.type(), function.name().value(), isInternal,
        function.isCacheable(), codeLocation);
    this.algorithm = new NativeCallAlgorithm(function);
  }

  private static HashCode nativeCallWorkerHash(NativeFunction function) {
    return WorkerHashes.workerHash(NativeCallWorker.class, function.hash());
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return algorithm.execute(input, container);
  }
}
