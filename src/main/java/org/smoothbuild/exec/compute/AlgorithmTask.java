package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.base.Input.input;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.concurrent.Feeders.runWhenAllAvailable;

import java.util.List;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.TaskSupplier;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class AlgorithmTask extends Task {
  private final Algorithm algorithm;

  public AlgorithmTask(TaskKind kind, Type type, String name, Algorithm algorithm,
      List<? extends TaskSupplier> dependencies, Location location) {
    super(kind, type, name, dependencies, location);
    this.algorithm = algorithm;
  }

  public Algorithm algorithm() {
    return algorithm;
  }

  @Override
  public Feeder<Obj> startComputation(Worker worker) {
    FeedingConsumer<Obj> result = new FeedingConsumer<>();
    ImmutableList<Feeder<Obj>> dependencyResults =
        map(dependencies(), d -> d.getTask().startComputation(worker));
    runWhenAllAvailable(dependencyResults,
        () -> worker.enqueueComputation(this, toInput(dependencyResults), result));
    return result;
  }

  private static Input toInput(List<Feeder<Obj>> results) {
    return input(map(results, Feeder::get));
  }

  @Override
  public String toString() {
    return "Task(" + algorithm.getClass().getCanonicalName() + ")";
  }
}