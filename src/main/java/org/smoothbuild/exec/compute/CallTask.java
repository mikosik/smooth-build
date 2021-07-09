package org.smoothbuild.exec.compute;

import static com.google.common.collect.ImmutableList.toImmutableList;

import java.util.List;

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
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class CallTask extends Task {
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
  public Feeder<Obj> startComputation(Worker worker) {
    FeedingConsumer<Obj> result = new FeedingConsumer<>();
    Feeder<Obj> functionFeeder = dependencies.get(0).getTask().startComputation(worker);
    functionFeeder.addConsumer(o -> onFunctionAvailable((Tuple) o, worker, result));
    return result;
  }

  private void onFunctionAvailable(Tuple functionTuple, Worker worker, FeedingConsumer<Obj> result) {
    String functionName = FunctionTuple.name(functionTuple).jValue();
    ImmutableList<TaskSupplier> arguments = dependencies().stream()
        .skip(1)
        .collect(toImmutableList());
    Task task = expressionToTaskConverter.taskForCall(scope, variables, type(), functionName,
        arguments, location());
    task.startComputation(worker).addConsumer(result);
  }
}
