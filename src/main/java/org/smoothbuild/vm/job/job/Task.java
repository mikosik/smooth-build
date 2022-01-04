package org.smoothbuild.vm.job.job;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.job.algorithm.Algorithm;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

import com.google.common.collect.ImmutableList;

public class Task extends AbstractJob {
  private final Algorithm algorithm;
  private final ImmutableList<Job> depJs;
  private final TaskInfo info;

  public Task(Algorithm algorithm, ImmutableList<Job> depJs, TaskInfo info) {
    super(algorithm.outputT(), info.loc());
    this.algorithm = algorithm;
    this.depJs = depJs;
    this.info = info;
  }

  public Algorithm algorithm() {
    return algorithm;
  }

  public TaskInfo info() {
    return info;
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    var depResults = map(depJs, d -> d.schedule(worker));
    runWhenAllAvailable(depResults, () -> worker.enqueue(info, algorithm, depResults, result));
    return result;
  }
}
