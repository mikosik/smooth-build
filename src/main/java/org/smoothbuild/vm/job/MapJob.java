package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.bytecode.obj.cnst.ArrayB;
import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
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
  public Promise<CnstB> scheduleImpl(Worker worker) {
    PromisedValue<CnstB> result = new PromisedValue<>();
    Promise<CnstB> array = arrayJ.schedule(worker);
    Promise<CnstB> func = funcJ.schedule(worker);
    runWhenAllAvailable(list(array, func),
        () -> onDepsCompleted((ArrayB) array.get(), worker, result));
    return result;
  }

  private void onDepsCompleted(ArrayB array, Worker worker, Consumer<CnstB> result) {
    var outputArrayT = (ArrayTB) type();
    var outputElemT = outputArrayT.elem();
    var mapElemJs = map(
        array.elems(CnstB.class),
        o -> mapElementJob(outputElemT, o));
    var info = new TaskInfo(TaskKind.ORDER, "[]", loc());
    jobCreator.orderEager(outputArrayT, mapElemJs, info)
        .schedule(worker)
        .addConsumer(result);
  }

  private Job mapElementJob(TypeB elemT, CnstB elem) {
    var elemJ = elemJob(elem);
    return jobCreator.callEagerJob(elemT, funcJ, list(elemJ), funcJ.loc(), params);
  }

  private Job elemJob(CnstB elem) {
    return new DummyJob(elem, arrayJ.loc());
  }
}
