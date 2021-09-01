package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.BUILDER;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.exec.base.LambdaRec;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.TaskCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;

public class DefaultArgumentTask extends StepTask {
  private final int index;
  private final Scope<Task> scope;
  private final TaskCreator taskCreator;

  public DefaultArgumentTask(Type type, String name, List<Task> dependencies, int index,
      Location location, Scope<Task> scope, TaskCreator taskCreator) {
    super(BUILDER, type, "building:" + name, dependencies, location);
    this.index = index;
    this.scope = scope;
    this.taskCreator = taskCreator;
  }

  @Override
  protected void onCompleted(Obj obj, Worker worker, Consumer<Obj> result) {
    String functionName = LambdaRec.name(((Rec) obj)).jValue();
    Task task = taskCreator.defaultArgumentEagerTask(scope, functionName, index);
    task.compute(worker).addConsumer(result);
  }
}
