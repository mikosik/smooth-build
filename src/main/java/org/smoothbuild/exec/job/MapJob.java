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
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.type.impl.ArrayTypeS;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.collect.Labeled;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class MapJob extends AbstractJob {
  private static final String MAP_TASK_NAME = MAP_FUNCTION_NAME + PARENTHESES;
  private final Scope<Labeled<Job>> scope;
  private final JobCreator jobCreator;

  public MapJob(TypeS type, List<Job> dependencies, Location location, Scope<Labeled<Job>> scope,
      JobCreator jobCreator) {
    super(type, dependencies, new NalImpl("building:" + MAP_TASK_NAME, location));
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    PromisedValue<ValueH> result = new PromisedValue<>();
    Promise<ValueH> array = arrayJob().schedule(worker);
    Promise<ValueH> function = functionJob().schedule(worker);
    runWhenAllAvailable(list(array, function),
        () -> onArrayCompleted((ArrayH) array.get(), (TupleH) function.get(), worker, result));
    return result;
  }

  private void onArrayCompleted(ArrayH array, TupleH function, Worker worker, Consumer<ValueH> result) {
    var outputArrayType = (ArrayTypeS) type();
    var outputElemType = outputArrayType.element();
    Job functionJob = getJob(function);
    var mapElemJobs = map(
        array.elements(ValueH.class),
        o -> mapElementJob(outputElemType, functionJob, o));
    var info = new TaskInfo(CALL, MAP_TASK_NAME, location());
    jobCreator.arrayEager(outputArrayType, mapElemJobs, info)
        .schedule(worker)
        .addConsumer(result);
  }

  private Job getJob(TupleH function) {
    return new DummyJob(functionJob().type(), function, functionJob());
  }

  private Job mapElementJob(TypeS elemType, Job functionJob, ValueH element) {
    Job elemJob = elemJob(elemType, element, arrayJob().location());
    return jobCreator.callEagerJob(scope, functionJob, list(elemJob), functionJob.location());
  }

  private Job elemJob(TypeS elemType, ValueH element, Location location) {
    return new DummyJob(elemType, element, new NalImpl("element-to-map", location));
  }

  private Job arrayJob() {
    return dependencies().get(0);
  }

  private Job functionJob() {
    return dependencies().get(1);
  }
}
