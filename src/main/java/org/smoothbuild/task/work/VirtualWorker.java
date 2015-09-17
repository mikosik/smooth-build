package org.smoothbuild.task.work;

import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.ContainerImpl;

public class VirtualWorker extends TaskWorker {

  public VirtualWorker(DefinedFunction function, CodeLocation codeLocation) {
    super(WorkerHashes.workerHash(VirtualWorker.class), function.type(), function.name().value(),
        false, true, codeLocation);
  }

  @Override
  public TaskOutput execute(TaskInput input, ContainerImpl container) {
    return new TaskOutput(input.values().get(0));
  }
}
