package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class VirtualTask extends Task {
  private final Task task;
  private final TaskKind kind;

  public VirtualTask(String name, Task task, TaskKind kind, Location location) {
    super(task.type(), name, ImmutableList.of(task), location);
    this.task = task;
    this.kind = kind;
  }

  @Override
  public Feeder<Obj> startComputation(Worker worker) {
    FeedingConsumer<Obj> result = new FeedingConsumer<>();
    task.startComputation(worker).addConsumer(
        sObject -> {
          worker.reporter().print(this, ResultSource.GROUP, List.of());
          result.accept(sObject);
        });
    return result;
  }

  @Override
  public TaskKind kind() {
    return kind;
  }
}
