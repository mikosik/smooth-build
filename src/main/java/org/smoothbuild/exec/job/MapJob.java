package org.smoothbuild.exec.job;

import static org.smoothbuild.exec.job.TaskKind.CALL;
import static org.smoothbuild.lang.base.define.FuncS.PARENTHESES;
import static org.smoothbuild.lang.base.define.MapFuncS.MAP_FUNCTION_NAME;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class MapJob extends AbstractJob {
  private static final String MAP_TASK_NAME = MAP_FUNCTION_NAME + PARENTHESES;
  private final IndexedScope<Job> scope;
  private final JobCreator jobCreator;

  public MapJob(TypeH typeS, Loc loc, List<Job> deps, IndexedScope<Job> scope,
      JobCreator jobCreator) {
    super(typeS, deps, new NalImpl("building:" + MAP_TASK_NAME, loc));
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    PromisedValue<ValueH> result = new PromisedValue<>();
    Promise<ValueH> array = arrayJob().schedule(worker);
    Promise<ValueH> func = funcJob().schedule(worker);
    runWhenAllAvailable(list(array, func),
        () -> onArrayCompleted((ArrayH) array.get(), (FuncH) func.get(), worker, result));
    return result;
  }

  private void onArrayCompleted(ArrayH array, FuncH funcH, Worker worker,
      Consumer<ValueH> result) {
    var outputArrayTypeH = (ArrayTypeH) type();
    var outputElemType = outputArrayTypeH.elem();
    var funcJob = getJob(funcH);
    var mapElemJobs = map(
        array.elems(ValueH.class),
        o -> mapElementJob(outputElemType, funcJob, o));
    var info = new TaskInfo(CALL, MAP_TASK_NAME, loc());
    jobCreator.orderEager(outputArrayTypeH, mapElemJobs, info)
        .schedule(worker)
        .addConsumer(result);
  }

  private Job getJob(FuncH func) {
    var funcJob = funcJob();
    return new DummyJob(funcJob.type(), func, funcJob);
  }

  private Job mapElementJob(TypeH elemType, Job funcJob, ValueH elem) {
    var elemJob = elemJob(elemType, elem, arrayJob().loc());
    return jobCreator.callEagerJob(scope, funcJob, list(elemJob), funcJob.loc());
  }

  private Job elemJob(TypeH elemType, ValueH elem, Loc loc) {
    return new DummyJob(elemType, elem, new NalImpl("elem-to-map", loc));
  }

  private Job arrayJob() {
    return deps().get(0);
  }

  private Job funcJob() {
    return deps().get(1);
  }
}
