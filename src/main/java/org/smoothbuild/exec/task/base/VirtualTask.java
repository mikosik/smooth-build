package org.smoothbuild.exec.task.base;

import static org.smoothbuild.exec.task.base.TaskKind.CALL;

import java.util.List;

import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

public class VirtualTask extends NonComputableTask {
  private final Task task;

  public VirtualTask(String name, ConcreteType type, Task task, Location location) {
    super(name, type, ImmutableList.of(task), location);
    this.task = task;
  }

  @Override
  public Feeder<SObject> startComputation(Worker worker) {
    Feeder<SObject> result = new Feeder<>();
    task.startComputation(worker).addConsumer(
        sObject -> {
          worker.reporter().print(this, false, List.of());
          result.accept(sObject);
        });
    return result;
  }

  @Override
  public TaskKind kind() {
    return CALL;
  }
}
