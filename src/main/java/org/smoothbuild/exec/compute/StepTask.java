package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

public abstract class StepTask extends RealTask {
  public StepTask(TaskKind kind, Type type, String name, List<Task> dependencies,
      Location location) {
    super(kind, type, name, dependencies, location);
  }

  @Override
  public Feeder<Obj> compute(Worker worker) {
    FeedingConsumer<Obj> result = new FeedingConsumer<>();
    dependencies().get(0)
        .compute(worker)
        .addConsumer(obj -> notifyCompleted(obj, worker, result));
    return result;
  }

  private void notifyCompleted(Obj obj, Worker worker, Consumer<Obj> result) {
    worker.reporter().print(this, list());
    onCompleted(obj, worker, result);
  }

  protected abstract void onCompleted(Obj obj, Worker worker, Consumer<Obj> result);
}
