package org.smoothbuild.exec.task.base;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.exec.comp.Input.input;
import static org.smoothbuild.util.concurrent.Feeder.runWhenAllAvailable;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

public class NormalTask extends ComputableTask {
  public NormalTask(Algorithm algorithm, List<? extends BuildTask> dependencies, Location location,
      boolean cacheable) {
    super(algorithm, dependencies, location, cacheable);
  }

  @Override
  public Feeder<SObject> startComputation(ParallelTaskExecutor.Worker worker) {
    Feeder<SObject> result = new Feeder<>();
    ImmutableList<Feeder<SObject>> childrenResults = children()
        .stream()
        .map(ch -> ch.startComputation(worker))
        .collect(toImmutableList());
    runWhenAllAvailable(childrenResults,
        () -> worker.enqueueComputation(this, toInput(childrenResults), result));
    return result;
  }

  private static Input toInput(List<Feeder<SObject>> results) {
    List<SObject> childValues = results
        .stream()
        .map(Feeder::value)
        .collect(toImmutableList());
    return input(childValues);
  }
}
