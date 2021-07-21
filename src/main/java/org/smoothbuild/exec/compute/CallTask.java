package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.skipFirst;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.exec.base.FunctionTuple;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.ExpressionToTaskConverter;
import org.smoothbuild.exec.plan.TaskSupplier;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.BoundedVariables;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;

public class CallTask extends StepTask {
  private final BoundedVariables variables;
  private final Scope<TaskSupplier> scope;
  private final ExpressionToTaskConverter expressionToTaskConverter;

  public CallTask(TaskKind kind, Type type, String name, List<? extends TaskSupplier> dependencies,
      Location location, BoundedVariables variables, Scope<TaskSupplier> scope,
      ExpressionToTaskConverter expressionToTaskConverter) {
    super(kind, type, name, dependencies, location);
    this.variables = variables;
    this.scope = scope;
    this.expressionToTaskConverter = expressionToTaskConverter;
  }

  @Override
  protected void onCompleted(Obj obj, Worker worker, Consumer<Obj> result) {
    String functionName = FunctionTuple.name(((Tuple) obj)).jValue();
    Task task = expressionToTaskConverter.taskForCall(
        scope, variables, type(), functionName, skipFirst(dependencies), location());
    task.startComputation(worker).addConsumer(result);
  }
}
