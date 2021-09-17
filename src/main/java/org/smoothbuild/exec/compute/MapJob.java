package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.compute.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.Function.PARENTHESES;
import static org.smoothbuild.lang.base.define.MapFunction.MAP_FUNCTION_NAME;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;
import static org.smoothbuild.util.concurrent.Feeders.runWhenAllAvailable;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.NamedImpl;
import org.smoothbuild.lang.base.type.ArrayType;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

public class MapJob extends AbstractJob {
  private static final String MAP_TASK_NAME = MAP_FUNCTION_NAME + PARENTHESES;
  private final Scope<Job> scope;
  private final JobCreator jobCreator;

  public MapJob(Type type, List<Job> dependencies, Location location, Scope<Job> scope,
      JobCreator jobCreator) {
    super(type, dependencies, new NamedImpl("building:" + MAP_TASK_NAME, location));
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Feeder<Val> schedule(Worker worker) {
    FeedingConsumer<Val> result = new FeedingConsumer<>();
    Feeder<Val> array = arrayJob().schedule(worker);
    Feeder<Val> function = functionJob().schedule(worker);
    runWhenAllAvailable(list(array, function),
        () -> onArrayCompleted((Array) array.get(), (Rec) function.get(), worker, result));
    return result;
  }

  private void onArrayCompleted(Array array, Rec lambda, Worker worker,
      Consumer<Val> result) {
    var outputArrayType = (ArrayType) type();
    var outputElemType = outputArrayType.elemType();
    Job lambdaJob = getJob(lambda);
    var mapElemJobs = map(
        array.elements(Val.class),
        o -> mapElementJob(outputElemType, lambdaJob, o));
    var info = new TaskInfo(CALL, MAP_TASK_NAME, location());
    jobCreator.arrayEager(outputArrayType, mapElemJobs, info)
        .schedule(worker)
        .addConsumer(result);
  }

  private Job getJob(Rec lambda) {
    return new DummyJob(functionJob().type(), lambda, functionJob());
  }

  private Job mapElementJob(Type elemType, Job lambdaJob, Val element) {
    Job elemJob = elemJob(elemType, element, arrayJob().location());
    return jobCreator.callEagerJob(scope, lambdaJob, list(elemJob), lambdaJob.location());
  }

  private Job elemJob(Type elemType, Val element, Location location) {
    return new DummyJob(elemType, element, new NamedImpl("element-to-map", location));
  }

  private Job arrayJob() {
    return dependencies().get(0);
  }

  private Job functionJob() {
    return dependencies().get(1);
  }
}
