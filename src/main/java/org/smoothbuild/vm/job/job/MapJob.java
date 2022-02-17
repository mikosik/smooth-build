package org.smoothbuild.vm.job.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;
import static org.smoothbuild.vm.job.job.TaskKind.ORDER;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.job.JobCreator;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public class MapJob extends AbstractJob {
  private final Job arrayJ;
  private final Job funcJ;
  private final List<Job> params;
  private final JobCreator jobCreator;

  public MapJob(TypeB type, Job arrayJ, Job funcJ, Loc loc, List<Job> params,
      JobCreator jobCreator) {
    super(type, loc);
    this.arrayJ = arrayJ;
    this.funcJ = funcJ;
    this.params = params;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValB> scheduleImpl(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    Promise<ValB> array = arrayJ.schedule(worker);
    Promise<ValB> func = funcJ.schedule(worker);
    runWhenAllAvailable(list(array, func),
        () -> onDepsCompleted((ArrayB) array.get(), worker, result));
    return result;
  }

  private void onDepsCompleted(ArrayB array, Worker worker, Consumer<ValB> result) {
    var outputArrayT = (ArrayTB) type();
    var outputElemT = outputArrayT.elem();
    var mapElemJs = map(
        array.elems(ValB.class),
        o -> mapElementJob(outputElemT, o));
    var info = new TaskInfo(ORDER, "[]", loc());
    jobCreator.orderEager(outputArrayT, mapElemJs, info)
        .schedule(worker)
        .addConsumer(result);
  }

  private Job mapElementJob(TypeB elemT, ValB elem) {
    var elemJ = elemJob(elem);
    return jobCreator.callEagerJob(elemT, funcJ, list(elemJ), funcJ.loc(), params);
  }

  private Job elemJob(ValB elem) {
    return new DummyJob(elem, arrayJ.loc());
  }
}
