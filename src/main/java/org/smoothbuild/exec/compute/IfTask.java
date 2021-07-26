package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.ResultSource.EXECUTION;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.Function.PARENTHESES;
import static org.smoothbuild.lang.base.define.InternalModule.IF_FUNCTION_NAME;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Bool;
import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.TaskSupplier;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

public class IfTask extends StepTask {

  public IfTask(Type type, List<? extends TaskSupplier> dependencies, Location location) {
    super(CALL, type, IF_FUNCTION_NAME + PARENTHESES, dependencies, location);
  }

  @Override
  protected void onCompleted(Obj obj, Worker worker, Consumer<Obj> result) {
    boolean condition = ((Bool) obj).jValue();
    Task subTaskToCompute = condition ? thenTask() : elseTask();
    subTaskToCompute.startComputation(worker)
        .chain((o) -> {
          worker.reporter().print(this, EXECUTION, list());
          return o;
        })
        .addConsumer(result);
  }

  private Task thenTask() {
    return dependencies().get(1).getTask();
  }

  private Task elseTask() {
    return dependencies().get(2).getTask();
  }
}
