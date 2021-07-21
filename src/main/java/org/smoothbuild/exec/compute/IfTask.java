package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.Function.PARENTHESES;

import java.util.List;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.TaskSupplier;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

public class IfTask extends Task {
  public static final String IF_FUNCTION_NAME = "if";

  public IfTask(Type type, List<? extends TaskSupplier> dependencies, Location location) {
    super(CALL, type, IF_FUNCTION_NAME + PARENTHESES, dependencies, location);
  }

  @Override
  public Feeder<Obj> startComputation(Worker worker) {
    FeedingConsumer<Obj> result = new FeedingConsumer<>();
    Feeder<Obj> conditionResult = conditionTask().startComputation(worker);
    conditionResult.addConsumer(o -> onCompleted((Bool) o, worker, result));
    return result;
  }

  private void onCompleted(Bool condition, Worker worker, FeedingConsumer<Obj> result) {
    Task subTaskToCompute = condition.jValue() ? thenTask() : elseTask();
    subTaskToCompute.startComputation(worker).addConsumer(result);
  }

  private Task conditionTask() {
    return dependencies().get(0).getTask();
  }

  private Task thenTask() {
    return dependencies().get(1).getTask();
  }

  private Task elseTask() {
    return dependencies().get(2).getTask();
  }
}
