package org.smoothbuild.vm.job.job;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.job.algorithm.Algorithm;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class Task extends AbstractJob {
  private final TaskInfo info;
  private final Algorithm algorithm;

  public Task(List<Job> deps, TaskInfo info, Algorithm algorithm) {
    super(algorithm.outputT(), deps, info);
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
    var depResults = map(deps(), d -> d.schedule(worker));
    runWhenAllAvailable(depResults, () -> worker.enqueue(info, algorithm, depResults, result));
    return result;
  }
}
