package org.smoothbuild.exec.job;

import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class Task extends AbstractJob {
  private final TaskInfo info;
  private final Algorithm algorithm;

  public Task(Type type, List<Job> dependencies, TaskInfo info, Algorithm algorithm) {
    super(type, dependencies, info);
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
  public Promise<Val> schedule(Worker worker) {
    PromisedValue<Val> result = new PromisedValue<>();
    var input = map(dependencies(), d -> d.schedule(worker));
    runWhenAllAvailable(input, () -> worker.enqueue(info, algorithm, input, result));
    return result;
  }
}
