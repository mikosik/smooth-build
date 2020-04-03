package org.smoothbuild.exec.task.base;

import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.task.parallel.ResultFeeder;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.ConcreteType;

import com.google.common.collect.ImmutableList;

public class VirtualTask extends NonComputableTask {
  private final Task task;

  public VirtualTask(String name, ConcreteType type, Task task, Location location) {
    super(name, type, ImmutableList.of(task), location);
    this.task = task;
  }

  @Override
  public ResultFeeder startComputation(Worker worker) {
    ResultFeeder resultFeeder = new ResultFeeder();
    task.startComputation(worker)
        .addOutputConsumer((v) -> resultFeeder.setResult(new MaybeOutput(v)));
    return resultFeeder;
  }
}
