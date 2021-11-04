package org.smoothbuild.exec.job;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.Function.PARENTHESES;
import static org.smoothbuild.lang.base.define.MapFunction.MAP_FUNCTION_NAME;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.type.impl.ArraySType;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class MapJob extends AbstractJob {
  private static final String MAP_TASK_NAME = MAP_FUNCTION_NAME + PARENTHESES;
  private final Scope<Job> scope;
  private final JobCreator jobCreator;

  public MapJob(TypeS type, List<Job> dependencies, Location location, Scope<Job> scope,
      JobCreator jobCreator) {
    super(type, dependencies, new NalImpl("building:" + MAP_TASK_NAME, location));
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<Val> schedule(Worker worker) {
    PromisedValue<Val> result = new PromisedValue<>();
    Promise<Val> array = arrayJob().schedule(worker);
    Promise<Val> function = functionJob().schedule(worker);
    runWhenAllAvailable(list(array, function),
        () -> onArrayCompleted((Array) array.get(), (Struc_) function.get(), worker, result));
    return result;
  }

  private void onArrayCompleted(Array array, Struc_ lambda, Worker worker,
      Consumer<Val> result) {
    var outputArrayType = (ArraySType) type();
    var outputElemType = outputArrayType.element();
    Job lambdaJob = getJob(lambda);
    var mapElemJobs = map(
        array.elements(Val.class),
        o -> mapElementJob(outputElemType, lambdaJob, o));
    var info = new TaskInfo(CALL, MAP_TASK_NAME, location());
    jobCreator.arrayEager(outputArrayType, mapElemJobs, info)
        .schedule(worker)
        .addConsumer(result);
  }

  private Job getJob(Struc_ lambda) {
    return new DummyJob(functionJob().type(), lambda, functionJob());
  }

  private Job mapElementJob(TypeS elemType, Job lambdaJob, Val element) {
    Job elemJob = elemJob(elemType, element, arrayJob().location());
    return jobCreator.callEagerJob(scope, lambdaJob, list(elemJob), lambdaJob.location());
  }

  private Job elemJob(TypeS elemType, Val element, Location location) {
    return new DummyJob(elemType, element, new NalImpl("element-to-map", location));
  }

  private Job arrayJob() {
    return dependencies().get(0);
  }

  private Job functionJob() {
    return dependencies().get(1);
  }
}
