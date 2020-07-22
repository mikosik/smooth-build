package org.smoothbuild.exec.task.base;

import static org.smoothbuild.exec.comp.Input.input;
import static org.smoothbuild.exec.task.base.TaskKind.CALL;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.task.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ConcreteType;
import org.smoothbuild.record.base.Bool;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class IfTask extends ComputableTask {
  public static final String IF_FUNCTION_NAME = "if";

  public IfTask(ConcreteType type, Algorithm algorithm, List<? extends Task> dependencies,
      Location location, boolean cacheable) {
    super(CALL, type, IF_FUNCTION_NAME, algorithm, dependencies, location, cacheable);
  }

  @Override
  public Feeder<Record> startComputation(Worker worker) {
    FeedingConsumer<Record> ifResult = new FeedingConsumer<>();
    Feeder<Record> conditionResult = conditionTask().startComputation(worker);
    Consumer<Record> ifEnqueuer = ifEnqueuer(worker, ifResult, conditionResult);
    Consumer<Record> thenOrElseEnqueuer = thenOrElseEnqueuer(worker, ifEnqueuer);
    conditionResult.addConsumer(thenOrElseEnqueuer);
    return ifResult;
  }

  private Consumer<Record> thenOrElseEnqueuer(Worker worker, Consumer<Record> ifEnqueuer) {
    return conditionValue -> {
      boolean condition = ((Bool) conditionValue).jValue();
      Task thenOrElseTask = condition ? thenTask() : elseTask();
      thenOrElseTask.startComputation(worker).addConsumer(ifEnqueuer);
    };
  }

  private Consumer<Record> ifEnqueuer(Worker worker, Consumer<Record> ifResultConsumer,
      Supplier<Record> conditionResult) {
    return thenOrElseResult -> {
      Record conditionValue = conditionResult.get();

      // Only one of then/else values will be used and it will be used twice.
      // This way TaskExecutor can calculate task hash and use it for caching.
      Input input = input(ImmutableList.of(conditionValue, thenOrElseResult, thenOrElseResult));
      worker.enqueueComputation(this, input, ifResultConsumer);
    };
  }

  private Task conditionTask() {
    return dependencies().get(0);
  }

  private Task thenTask() {
    return dependencies().get(1);
  }

  private Task elseTask() {
    return dependencies().get(2);
  }
}
