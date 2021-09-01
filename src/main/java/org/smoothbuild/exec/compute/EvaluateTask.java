package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.BUILDER;
import static org.smoothbuild.util.Lists.concat;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.exec.base.LambdaRec;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.TaskCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.BoundsMap;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;

public class EvaluateTask extends StepTask {
  private final List<Task> arguments;
  private final BoundsMap variables;
  private final Scope<Task> scope;
  private final TaskCreator taskCreator;

  public EvaluateTask(Type type, Task referencable, List<Task> arguments, Location location,
      BoundsMap variables, Scope<Task> scope, TaskCreator taskCreator) {
    super(BUILDER, type, "building-evaluation", concat(referencable, arguments), location);
    this.arguments = arguments;
    this.variables = variables;
    this.scope = scope;
    this.taskCreator = taskCreator;
  }

  @Override
  protected void onCompleted(Val val, Worker worker, Consumer<Val> result) {
    String name = LambdaRec.name(((Rec) val)).jValue();
    Task task = taskCreator.evaluateLambdaEagerTask(
        scope, variables, type(), name, arguments, location());
    task.compute(worker).addConsumer(result);
  }
}
