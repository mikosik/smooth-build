package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.concat;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.exec.base.FunctionTuple;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.ExpressionToTaskConverter;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.BoundedVariables;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;

public class CallTask extends StepTask {
  private final List<LazyTask> arguments;
  private final BoundedVariables variables;
  private final Scope<LazyTask> scope;
  private final ExpressionToTaskConverter expressionToTaskConverter;

  public CallTask(TaskKind kind, Type type, String name, Task function,
      List<LazyTask> arguments, Location location, BoundedVariables variables,
      Scope<LazyTask> scope, ExpressionToTaskConverter expressionToTaskConverter) {
    super(kind, type, name, createDependencies(function, arguments), location);
    this.arguments = arguments;
    this.variables = variables;
    this.scope = scope;
    this.expressionToTaskConverter = expressionToTaskConverter;
  }

  private static List<Dependency> createDependencies(Dependency function,
      List<? extends Dependency> arguments) {
    return concat(function, (List<Dependency>) arguments);
  }

  @Override
  protected void onCompleted(Obj obj, Worker worker, Consumer<Obj> result) {
    String functionName = FunctionTuple.name(((Tuple) obj)).jValue();
    Task task = expressionToTaskConverter.taskForNamedFunctionCall(
        scope, variables, type(), functionName, arguments, location());
    task.startComputation(worker).addConsumer(result);
  }
}
