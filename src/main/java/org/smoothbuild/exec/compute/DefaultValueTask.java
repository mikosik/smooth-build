package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.VALUE;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.exec.base.FunctionTuple;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.ExpressionToTaskConverter;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;

public class DefaultValueTask extends StepTask {
  private final int index;
  private final Scope<LazyTask> scope;
  private final ExpressionToTaskConverter expressionToTaskConverter;

  public DefaultValueTask(Type type, String name, List<Dependency> dependencies, int index,
      Location location, Scope<LazyTask> scope,
      ExpressionToTaskConverter expressionToTaskConverter) {
    super(VALUE, type, name, dependencies, location);
    this.index = index;
    this.scope = scope;
    this.expressionToTaskConverter = expressionToTaskConverter;
  }

  @Override
  protected void onCompleted(Obj obj, Worker worker, Consumer<Obj> result) {
    String functionName = FunctionTuple.name(((Tuple) obj)).jValue();
    Task task = expressionToTaskConverter.taskForNamedFunctionParameterDefaultValue(
        scope, functionName, index);
    task.startComputation(worker).addConsumer(result);
  }
}
