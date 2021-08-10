package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.BUILDER;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.Function.PARENTHESES;
import static org.smoothbuild.lang.base.define.IfFunction.IF_FUNCTION_NAME;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

public class IfTask extends StepTask {
  private static final String IF_TASK_NAME = IF_FUNCTION_NAME + PARENTHESES;

  public IfTask(Type type, List<LazyTask> dependencies, Location location) {
    super(BUILDER, type, "building:" + IF_TASK_NAME, dependencies, location);
  }

  @Override
  protected void onCompleted(Obj obj, Worker worker, Consumer<Obj> result) {
    boolean condition = ((Bool) obj).jValue();
    Task subTaskToCompute = condition ? thenTask() : elseTask();
    Task task = new VirtualTask(CALL, IF_TASK_NAME, subTaskToCompute, location());
    task.startComputation(worker)
        .addConsumer(result);
  }

  private Task thenTask() {
    return dependencies().get(1).task();
  }

  private Task elseTask() {
    return dependencies().get(2).task();
  }
}
