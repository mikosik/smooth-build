package org.smoothbuild.task.work;

import java.util.List;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class NativeCallWorker extends TaskWorker {
  private final NativeFunction function;

  public NativeCallWorker(NativeFunction function, boolean isInternal, CodeLocation codeLocation) {
    super(nativeCallWorkerHash(function), function.type(), function.name().value(), isInternal,
        function.isCacheable(), codeLocation);
    this.function = function;
  }

  private static HashCode nativeCallWorkerHash(NativeFunction function) {
    return WorkerHashes.workerHash(NativeCallWorker.class, function.hash());
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    Value result = function.invoke(container, calculateArguments(input));
    return new TaskOutput(result, container.messages());
  }

  private List<Value> calculateArguments(TaskInput input) {
    return input.values();
  }
}
