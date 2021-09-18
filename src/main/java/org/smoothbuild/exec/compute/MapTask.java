package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.BUILDER;
import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.Function.PARENTHESES;
import static org.smoothbuild.lang.base.define.MapFunction.MAP_FUNCTION_NAME;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.exec.plan.TaskCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

import com.google.common.collect.ImmutableList;

public class MapTask extends RealTask {
  private static final String MAP_TASK_NAME = MAP_FUNCTION_NAME + PARENTHESES;
  private final Scope<Task> scope;
  private final TaskCreator taskCreator;

  public MapTask(Type type, List<Task> dependencies, Location location, Scope<Task> scope,
      TaskCreator taskCreator) {
    super(BUILDER, type, "building:" + MAP_TASK_NAME, dependencies, location);
    this.scope = scope;
    this.taskCreator = taskCreator;
  }

  @Override
  public Feeder<Val> compute(Worker worker) {
    FeedingConsumer<Val> result = new FeedingConsumer<>();
    // functionTask is started, but we don't add any consumer waiting for it here.
    // Each task that actually maps single array element using that function will depend
    // on functionTask. We start it here to potentially so it executes in background so
    // is quicker available to element mapping tasks.
    functionTask().compute(worker);

    arrayTask()
        .compute(worker)
        .addConsumer(obj -> onArrayCompleted(obj, worker, result));
    return result;
  }

  private void onArrayCompleted(Val val, Worker worker, Consumer<Val> result) {
    Array array = (Array) val;
    ArrayType arrayType = (ArrayType) type();
    var elemTasks = map(array.elements(Val.class), o -> mapElementTask(arrayType.elemType(), o));
    taskCreator.arrayEagerTask(CALL, MAP_TASK_NAME, arrayType, elemTasks, location())
        .compute(worker)
        .addConsumer(result);
  }

  private Task mapElementTask(Type elemType, Val element) {
    ImmutableList<Task> argument = list(elemTask(elemType, element, arrayTask().location()));
    return taskCreator.callEagerTask(scope, functionTask(), argument, functionTask().location());
  }

  private Task elemTask(Type elemType, Val element, Location location) {
    return new DummyTask(BUILDER, elemType, "element-to-map", element, location);
  }

  private Task arrayTask() {
    return dependencies().get(0);
  }

  private Task functionTask() {
    return dependencies().get(1);
  }
}
