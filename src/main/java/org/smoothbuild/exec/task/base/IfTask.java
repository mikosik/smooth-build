package org.smoothbuild.exec.task.base;

import static org.smoothbuild.exec.comp.Input.input;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;

import com.google.common.collect.ImmutableList;

public class IfTask extends ComputableTask {
  public IfTask(Algorithm algorithm, List<? extends BuildTask> dependencies, Location location,
      boolean cacheable) {
    super(algorithm, dependencies, location, cacheable);
  }

  @Override
  public Feeder<SObject> startComputation(Worker worker) {
    Feeder<SObject> ifResult = new Feeder<>();
    Feeder<SObject> conditionResult = conditionChild().startComputation(worker);
    conditionResult.addConsumer(
        thenOrElseEnqueuer(worker, ifJobEnqueuer(worker, ifResult, conditionResult)));
    return ifResult;
  }

  private Consumer<SObject> thenOrElseEnqueuer(Worker worker, Consumer<SObject> ifJobEnqueuer) {
    return conditionValue -> {
      boolean condition = ((Bool) conditionValue).jValue();
      BuildTask thenOrElseTask = condition ? thenChild() : elseChild();
      thenOrElseTask.startComputation(worker).addConsumer(ifJobEnqueuer);
    };
  }

  private Consumer<SObject> ifJobEnqueuer(Worker worker, Feeder<SObject> ifResult,
      Feeder<SObject> conditionResult) {
    return thenOrElseValue -> {
      SObject conditionValue = conditionResult.value();

      // Only one of then/else values will be used and it will be used twice.
      // This way TaskExecutor can calculate task hash and use it for caching.
      Input input = input(ImmutableList.of(conditionValue, thenOrElseValue, thenOrElseValue));
      worker.enqueueComputation(this, input, ifResult);
    };
  }

  private BuildTask conditionChild() {
    return children().get(0);
  }

  private BuildTask thenChild() {
    return children().get(1);
  }

  private BuildTask elseChild() {
    return children().get(2);
  }
}
