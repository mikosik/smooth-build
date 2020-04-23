package org.smoothbuild.exec.task.base;

import static org.smoothbuild.exec.task.base.TaskKind.BUILDING_LITERAL;

import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

public class FixedTask extends NonComputableTask {
  private final SObject sObject;

  public FixedTask(SObject sObject, String name, Location location) {
    super(name, sObject.type(), ImmutableList.of(), location);
    this.sObject = sObject;
  }

  @Override
  public Feeder<SObject> startComputation(Worker worker) {
    Feeder<SObject> result = new Feeder<>();
    result.accept(sObject);
    return result;
  }

  @Override
  public TaskKind kind() {
    return BUILDING_LITERAL;
  }
}
