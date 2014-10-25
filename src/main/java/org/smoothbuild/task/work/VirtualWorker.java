package org.smoothbuild.task.work;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.def.DefinedFunction;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskInput;
import org.smoothbuild.task.base.TaskOutput;
import org.smoothbuild.task.exec.NativeApiImpl;

public class VirtualWorker<T extends SValue> extends TaskWorker<T> {

  public VirtualWorker(DefinedFunction<T> function, CodeLocation codeLocation) {
    super(WorkerHashes.workerHash(VirtualWorker.class), function.type(), function.name().value(),
        false, true, codeLocation);
  }

  @Override
  public TaskOutput<T> execute(TaskInput input, NativeApiImpl nativeApi) {
    @SuppressWarnings("unchecked")
    T output = (T) input.values().get(0);
    return new TaskOutput<>(output);
  }
}
