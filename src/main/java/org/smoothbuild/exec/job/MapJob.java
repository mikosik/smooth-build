package org.smoothbuild.exec.job;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.FunctionS.PARENTHESES;
import static org.smoothbuild.lang.base.define.MapFunctionS.MAP_FUNCTION_NAME;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class MapJob extends AbstractJob {
  private static final String MAP_TASK_NAME = MAP_FUNCTION_NAME + PARENTHESES;
  private final IndexedScope<Job> scope;
  private final JobCreator jobCreator;

  public MapJob(TypeHV typeS, Location location, List<Job> dependencies, IndexedScope<Job> scope,
      JobCreator jobCreator) {
    super(typeS, dependencies, new NalImpl("building:" + MAP_TASK_NAME, location));
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    PromisedValue<ValueH> result = new PromisedValue<>();
    Promise<ValueH> array = arrayJob().schedule(worker);
    Promise<ValueH> function = functionJob().schedule(worker);
    runWhenAllAvailable(list(array, function),
        () -> onArrayCompleted((ArrayH) array.get(), (FunctionH) function.get(), worker, result));
    return result;
  }

  private void onArrayCompleted(ArrayH array, FunctionH functionH, Worker worker,
      Consumer<ValueH> result) {
    var outputArrayTypeH = (ArrayTypeH) type();
    var outputElemType = outputArrayTypeH.element();
    var funcJob = getJob(functionH);
    var mapElemJobs = map(
        array.elements(ValueH.class),
        o -> mapElementJob(outputElemType, funcJob, o));
    var info = new TaskInfo(CALL, MAP_TASK_NAME, location());
    jobCreator.orderEager(outputArrayTypeH, mapElemJobs, info)
        .schedule(worker)
        .addConsumer(result);
  }

  private Job getJob(FunctionH function) {
    var funcJob = functionJob();
    return new DummyJob(funcJob.type(), function, funcJob);
  }

  private Job mapElementJob(TypeHV elemType, Job functionJob, ValueH element) {
    var elemJob = elemJob(elemType, element, arrayJob().location());
    return jobCreator.callEagerJob(scope, functionJob, list(elemJob), functionJob.location());
  }

  private Job elemJob(TypeHV elemType, ValueH element, Location location) {
    return new DummyJob(elemType, element, new NalImpl("element-to-map", location));
  }

  private Job arrayJob() {
    return dependencies().get(0);
  }

  private Job functionJob() {
    return dependencies().get(1);
  }
}
