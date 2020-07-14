package org.smoothbuild.exec.task.base;

import static org.smoothbuild.exec.comp.Input.input;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class IfTask extends ComputableTask {
  public static final String IF_FUNCTION_NAME = "if";

  public IfTask(ConcreteType type, Algorithm algorithm, List<? extends Task> dependencies,
      Location location, boolean cacheable) {
    super(type, IF_FUNCTION_NAME, algorithm, dependencies, location, cacheable);
  }

  @Override
  public Feeder<SObject> startComputation(Worker worker) {
    FeedingConsumer<SObject> ifResult = new FeedingConsumer<>();
    Feeder<SObject> conditionResult = conditionChild().startComputation(worker);
    Consumer<SObject> ifEnqueuer = ifEnqueuer(worker, ifResult, conditionResult);
    Consumer<SObject> thenOrElseEnqueuer = thenOrElseEnqueuer(worker, ifEnqueuer);
    conditionResult.addConsumer(thenOrElseEnqueuer);
    return ifResult;
  }

  private Consumer<SObject> thenOrElseEnqueuer(Worker worker, Consumer<SObject> ifEnqueuer) {
    return conditionValue -> {
      boolean condition = ((Bool) conditionValue).jValue();
      Task thenOrElseTask = condition ? thenChild() : elseChild();
      thenOrElseTask.startComputation(worker).addConsumer(ifEnqueuer);
    };
  }

  private Consumer<SObject> ifEnqueuer(Worker worker, Consumer<SObject> ifResultConsumer,
      Supplier<SObject> conditionResult) {
    return thenOrElseResult -> {
      SObject conditionValue = conditionResult.get();

      // Only one of then/else values will be used and it will be used twice.
      // This way TaskExecutor can calculate task hash and use it for caching.
      Input input = input(ImmutableList.of(conditionValue, thenOrElseResult, thenOrElseResult));
      worker.enqueueComputation(this, input, ifResultConsumer);
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
