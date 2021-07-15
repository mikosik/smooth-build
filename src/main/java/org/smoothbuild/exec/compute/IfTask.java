package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.base.Input.input;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.Function.PARENTHESES;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.concurrent.Feeders.runWhenAllAvailable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.smoothbuild.db.object.base.Bool;
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

public class IfTask extends ComputableTask {
  public static final String IF_FUNCTION_NAME = "if";

  public IfTask(Type type, Algorithm algorithm, List<? extends TaskSupplier> dependencies,
      Location location) {
    super(CALL, type, IF_FUNCTION_NAME + PARENTHESES, algorithm, dependencies, location);
  }

  @Override
  public Feeder<Obj> startComputation(Worker worker) {
    FeedingConsumer<Obj> ifResult = new FeedingConsumer<>();
    Feeder<Obj> conditionResult = conditionTask().startComputation(worker);
    Feeder<Obj> nativeCodeResult = nativeCodeTask().startComputation(worker);
    Consumer<Obj> ifEnqueuer = ifEnqueuer(worker, ifResult, nativeCodeResult, conditionResult);
    Consumer<Obj> thenOrElseEnqueuer = thenOrElseEnqueuer(worker, ifEnqueuer);
    runWhenAllAvailable(list(conditionResult, nativeCodeResult),
        () -> thenOrElseEnqueuer.accept(conditionResult.get()));
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
      Supplier<Obj> nativeCodeResult, Supplier<Obj> conditionResult) {
    return thenOrElseResult -> {
      Obj nativeCodeValue = nativeCodeResult.get();
      Obj conditionValue = conditionResult.get();

      // Only one of then/else values will be used and it will be used twice.
      // This way TaskExecutor can calculate task hash and use it for caching.
      Input input = input(ImmutableList.of(
          nativeCodeValue, conditionValue, thenOrElseResult, thenOrElseResult));
      worker.enqueueComputation(this, input, ifResultConsumer);
    };
  }

  private Task nativeCodeTask() {
    return dependencies().get(0).getTask();
  }

  private Task conditionTask() {
    return dependencies().get(1).getTask();
  }

  private Task thenTask() {
    return dependencies().get(2).getTask();
  }

  private Task elseTask() {
    return dependencies().get(3).getTask();
  }
}
