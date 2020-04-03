package org.smoothbuild.exec.task.base;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.exec.comp.Input.input;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor;
import org.smoothbuild.exec.task.parallel.ResultFeeder;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.ThresholdRunnable;

import com.google.common.collect.ImmutableList;

public class NormalTask extends ComputableTask {
  public NormalTask(Algorithm algorithm, List<? extends Task> dependencies, Location location,
      boolean cacheable) {
    super(algorithm, dependencies, location, cacheable);
  }

  @Override
  public ResultFeeder startComputation(ParallelTaskExecutor.Worker worker) {
    ResultFeeder result = new ResultFeeder();
    ImmutableList<ResultFeeder> childrenResults = children()
        .stream()
        .map(ch -> ch.startComputation(worker))
        .collect(toImmutableList());
    ThresholdRunnable enqueuer = new ThresholdRunnable(childrenResults.size(),
        () -> worker.enqueueComputation(this, toInput(childrenResults), result));
    childrenResults.forEach(childResult -> childResult.addValueAvailableListener(enqueuer));
    return result;
  }

  private static Input toInput(List<ResultFeeder> results) {
    List<SObject> childValues = results
        .stream()
        .map(result -> result.output().value())
        .collect(toImmutableList());
    return input(childValues);
  }
}
