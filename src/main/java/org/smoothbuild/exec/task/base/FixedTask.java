package org.smoothbuild.exec.task.base;

import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.task.parallel.ResultFeeder;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;

import com.google.common.collect.ImmutableList;

public class FixedTask extends NonComputableTask {
  private final MaybeOutput result;

  public FixedTask(SObject object, String name, Array messages, Location location) {
    super(name, object.type(), ImmutableList.of(), location);
    this.result = new MaybeOutput(new Output(object, messages));
  }

  @Override
  public ResultFeeder startComputation(Worker worker) {
    ResultFeeder result = new ResultFeeder();
    result.setResult(this.result);
    return result;
  }
}
