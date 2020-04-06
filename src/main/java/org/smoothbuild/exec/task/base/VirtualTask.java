package org.smoothbuild.exec.task.base;

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
    return task.startComputation(worker);
  }
}
