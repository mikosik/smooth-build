package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;

import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class Task extends AbstractJob {
  private final TaskInfo info;
  private final Algorithm algorithm;

  public Task(TypeB type, List<Job> deps, TaskInfo info, Algorithm algorithm) {
    super(type, deps, info);
    this.info = info;
    this.algorithm = algorithm;
  }

  public TaskInfo info() {
    return info;
  }

  public Algorithm algorithm() {
    return algorithm;
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    var input = map(deps(), d -> d.schedule(worker));
    runWhenAllAvailable(input, () -> worker.enqueue(info, algorithm, input, result));
    return result;
  }
}
