package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.TaskSupplier;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class VirtualTask extends Task {
  private final TaskSupplier task;

  public VirtualTask(TaskKind kind, String name, TaskSupplier task, Location location) {
    super(kind, task.type(), name, ImmutableList.of(task), location);
    this.task = task;
  }

  @Override
  public Feeder<Obj> startComputation(Worker worker) {
    FeedingConsumer<Obj> result = new FeedingConsumer<>();
    task.getTask().startComputation(worker).addConsumer(
        obj -> {
          worker.reporter().print(this, ResultSource.GROUP, List.of());
          result.accept(obj);
        });
    return result;
  }
}
