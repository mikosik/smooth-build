package org.smoothbuild.exec.task.base;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.exec.comp.Input.input;
import static org.smoothbuild.util.concurrent.Feeders.runWhenAllAvailable;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.record.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class NormalTask extends ComputableTask {
  public NormalTask(ConcreteType type, String name, Algorithm algorithm,
      List<? extends Task> dependencies, Location location, boolean cacheable) {
    super(type, name, algorithm, dependencies, location, cacheable);
  }

  @Override
  public Feeder<SObject> startComputation(Worker worker) {
    FeedingConsumer<SObject> result = new FeedingConsumer<>();
    ImmutableList<Feeder<SObject>> dependencyResults = dependencies()
        .stream()
        .map(ch -> ch.startComputation(worker))
        .collect(toImmutableList());
    runWhenAllAvailable(dependencyResults,
        () -> worker.enqueueComputation(this, toInput(dependencyResults), result));
    return result;
  }

  private static Input toInput(List<Feeder<SObject>> results) {
    List<SObject> childValues = results
        .stream()
        .map(Feeder::get)
        .collect(toImmutableList());
    return input(childValues);
  }
}
