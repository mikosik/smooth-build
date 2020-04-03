package org.smoothbuild.exec.task.base;

import static org.smoothbuild.exec.comp.Input.input;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.task.parallel.ResultFeeder;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;

import com.google.common.collect.ImmutableList;

public class IfTask extends ComputableTask {
  public IfTask(Algorithm algorithm, List<? extends Task> dependencies, Location location,
      boolean cacheable) {
    super(algorithm, dependencies, location, cacheable);
  }

  @Override
  public ResultFeeder startComputation(Worker worker) {
    ResultFeeder ifResult = new ResultFeeder();
    ResultFeeder conditionResult = conditionChild().startComputation(worker);
    conditionResult.addValueConsumer(
        thenOrElseEnqueuer(worker, ifJobEnqueuer(worker, ifResult, conditionResult)));
    return ifResult;
  }

  private Consumer<SObject> thenOrElseEnqueuer(Worker worker, Consumer<SObject> ifJobEnqueuer) {
    return conditionValue -> {
      boolean condition = ((Bool) conditionValue).jValue();
      Task thenOrElseTask = condition ? thenChild() : elseChild();
      thenOrElseTask.startComputation(worker).addValueConsumer(ifJobEnqueuer);
    };
  }

  private Consumer<SObject> ifJobEnqueuer(Worker worker, ResultFeeder ifResult,
      ResultFeeder conditionResult) {
    return thenOrElseValue -> {
      SObject conditionValue = conditionResult.output().value();

      // Only one of then/else values will be used and it will be used twice.
      // This way TaskExecutor can calculate task hash and use it for caching.
      Input input = input(ImmutableList.of(conditionValue, thenOrElseValue, thenOrElseValue));
      worker.enqueueComputation(this, input, ifResult);
    };
  }

  private Task conditionChild() {
    return children().get(0);
  }

  private Task thenChild() {
    return children().get(1);
  }

  private Task elseChild() {
    return children().get(2);
  }
}
