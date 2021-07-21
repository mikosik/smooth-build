package org.smoothbuild.exec.compute;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.TaskSupplier;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

public abstract class StepTask extends Task {
  public StepTask(TaskKind kind, Type type, String name, List<? extends TaskSupplier> dependencies,
      Location location) {
    super(kind, type, name, dependencies, location);
  }

  @Override
  public Feeder<Obj> startComputation(Worker worker) {
    FeedingConsumer<Obj> result = new FeedingConsumer<>();
    dependencies().get(0).getTask()
        .startComputation(worker)
        .addConsumer(obj -> onCompleted(obj, worker, result));
    return result;
  }

  protected abstract void onCompleted(Obj obj, Worker worker, Consumer<Obj> result);
}
