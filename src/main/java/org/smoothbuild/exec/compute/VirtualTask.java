package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.CALL;

import java.util.List;

import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class VirtualTask extends Task {
  private final Task task;

  public VirtualTask(String name, ConcreteType type, Task task, Location location) {
    super(type, name, ImmutableList.of(task), location);
    this.task = task;
  }

  @Override
  public Feeder<Record> startComputation(Worker worker) {
    FeedingConsumer<Record> result = new FeedingConsumer<>();
    task.startComputation(worker).addConsumer(
        sObject -> {
          worker.reporter().print(this, ResultSource.GROUP, List.of());
          result.accept(sObject);
        });
    return result;
  }

  @Override
  public TaskKind kind() {
    return CALL;
  }
}
