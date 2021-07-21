package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.VALUE;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.exec.base.FunctionTuple;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.ExpressionToTaskConverter;
import org.smoothbuild.exec.plan.TaskSupplier;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;

public class DefaultValueTask extends StepTask {
  private final int index;
  private final Scope<TaskSupplier> scope;
  private final ExpressionToTaskConverter expressionToTaskConverter;

  public DefaultValueTask(Type type, String name, List<TaskSupplier> dependencies,
      int index, Location location, Scope<TaskSupplier> scope,
      ExpressionToTaskConverter expressionToTaskConverter) {
    super(VALUE, type, name, dependencies, location);
    this.index = index;
    this.scope = scope;
    this.expressionToTaskConverter = expressionToTaskConverter;
  }

  @Override
  protected void onCompleted(Obj obj, Worker worker, Consumer<Obj> result) {
    String functionName = FunctionTuple.name(((Tuple) obj)).jValue();
    TaskSupplier task = expressionToTaskConverter.taskForDefaultValue(scope, functionName, index);
    task.getTask().startComputation(worker).addConsumer(result);
  }
}
