package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.base.Input.input;
import static org.smoothbuild.lang.base.define.Callable.PARENTHESES;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class IfTask extends ComputableTask {
  public static final String IF_FUNCTION_NAME = "if";

  public IfTask(Type type, Algorithm algorithm, List<? extends Task> dependencies,
      Location location, boolean cacheable) {
    super(TaskKind.CALL, type, IF_FUNCTION_NAME + PARENTHESES, algorithm, dependencies, location,
        cacheable);
  }

  @Override
  public Feeder<Obj> startComputation(Worker worker) {
    FeedingConsumer<Obj> ifResult = new FeedingConsumer<>();
    Feeder<Obj> conditionResult = conditionTask().startComputation(worker);
    Consumer<Obj> ifEnqueuer = ifEnqueuer(worker, ifResult, conditionResult);
    Consumer<Obj> thenOrElseEnqueuer = thenOrElseEnqueuer(worker, ifEnqueuer);
    conditionResult.addConsumer(thenOrElseEnqueuer);
    return ifResult;
  }

  private Consumer<Obj> thenOrElseEnqueuer(Worker worker, Consumer<Obj> ifEnqueuer) {
    return conditionValue -> {
      boolean condition = ((Bool) conditionValue).jValue();
      Task thenOrElseTask = condition ? thenTask() : elseTask();
      thenOrElseTask.startComputation(worker).addConsumer(ifEnqueuer);
    };
  }

  private Consumer<Obj> ifEnqueuer(Worker worker, Consumer<Obj> ifResultConsumer,
      Supplier<Obj> conditionResult) {
    return thenOrElseResult -> {
      Obj conditionValue = conditionResult.get();

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
