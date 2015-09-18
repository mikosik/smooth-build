package org.smoothbuild.task.compute;

import java.util.List;

import org.smoothbuild.lang.function.nativ.NativeFunction;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

public class NativeCallAlgorithm implements Algorithm {
  private final NativeFunction function;

  public NativeCallAlgorithm(NativeFunction function) {
    this.function = function;
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
