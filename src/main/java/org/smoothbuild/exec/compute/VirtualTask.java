package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;

public class VirtualTask extends StepTask {
  public VirtualTask(TaskKind kind, String name, Task task, Location location) {
    super(kind, task.type(), name, list(task), location);
  }

  @Override
  protected void onCompleted(Obj obj, Worker worker, Consumer<Obj> result) {
    result.accept(obj);
  }
}
