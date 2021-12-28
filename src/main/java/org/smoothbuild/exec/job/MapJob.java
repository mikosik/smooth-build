package org.smoothbuild.exec.job;

import static org.smoothbuild.exec.job.TaskKind.INTERNAL;
import static org.smoothbuild.lang.base.define.FuncS.PARENTHESES;
import static org.smoothbuild.lang.base.define.MapFuncS.MAP_FUNCTION_NAME;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.function.Consumer;

import org.smoothbuild.db.bytecode.obj.val.ArrayB;
import org.smoothbuild.db.bytecode.obj.val.FuncB;
import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.db.bytecode.type.val.ArrayTB;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class MapJob extends AbstractJob {
  private static final String MAP_TASK_NAME = MAP_FUNCTION_NAME + PARENTHESES;
  private final Job arrayJ;
  private final Job funcJ;
  private final IndexedScope<Job> scope;
  private final JobCreator jobCreator;

  public MapJob(TypeB type, Job arrayJ, Job funcJ, Loc loc, IndexedScope<Job> scope,
      JobCreator jobCreator) {
    super(type, list(arrayJ, funcJ), new NalImpl("building:" + MAP_TASK_NAME, loc));
    this.arrayJ = arrayJ;
    this.funcJ = funcJ;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    Promise<ValB> array = arrayJ.schedule(worker);
    Promise<ValB> func = funcJ.schedule(worker);
    runWhenAllAvailable(list(array, func),
        () -> onArrayCompleted((ArrayB) array.get(), (FuncB) func.get(), worker, result));
    return result;
  }

  private void onArrayCompleted(ArrayB array, FuncB funcB, Worker worker,
      Consumer<ValB> result) {
    var outputArrayTypeH = (ArrayTB) type();
    var outputElemType = outputArrayTypeH.elem();
    var funcJob = getJob(funcB);
    var mapElemJobs = map(
        array.elems(ValB.class),
        o -> mapElementJob(outputElemType, funcJob, o));
    var info = new TaskInfo(INTERNAL, MAP_TASK_NAME, loc());
    jobCreator.orderEager(outputArrayTypeH, mapElemJobs, info)
        .schedule(worker)
        .addConsumer(result);
  }

  private Job getJob(FuncB func) {
    return new DummyJob(funcJ.type(), func, funcJ);
  }

  private Job mapElementJob(TypeB elemType, Job funcJob, ValB elem) {
    var elemJob = elemJob(elemType, elem, arrayJ.loc());
    return jobCreator.callEagerJob(scope, funcJob, list(elemJob), funcJob.loc());
  }

  private Job elemJob(TypeB elemType, ValB elem, Loc loc) {
    return new DummyJob(elemType, elem, new NalImpl("elem-to-map", loc));
  }
}
