package org.smoothbuild.vm.job.job;

import static org.smoothbuild.lang.base.define.FuncS.PARENTHESES;
import static org.smoothbuild.lang.base.define.MapFuncS.MAP_FUNCTION_NAME;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.job.JobCreator;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

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

  private void onArrayCompleted(ArrayB array, FuncB func, Worker worker, Consumer<ValB> result) {
    var outputArrayT = (ArrayTB) type();
    var outputElemT = outputArrayT.elem();
    var funcJ = getJob(func);
    var mapElemJs = map(
        array.elems(ValB.class),
        o -> mapElementJob(outputElemT, funcJ, o));
    var info = new TaskInfo(TaskKind.INTERNAL, MAP_TASK_NAME, loc());
    jobCreator.orderEager(outputArrayT, mapElemJs, info)
        .schedule(worker)
        .addConsumer(result);
  }

  private Job getJob(FuncB func) {
    return new DummyJob(funcJ.type(), func, funcJ);
  }

  private Job mapElementJob(TypeB elemT, Job funcJ, ValB elem) {
    var elemJ = elemJob(elem, arrayJ.loc());
    return jobCreator.callEagerJob(elemT, funcJ, list(elemJ), funcJ.loc(), scope);
  }

  private Job elemJob(ValB elem, Loc loc) {
    return new DummyJob(elem.type(), elem, new NalImpl("elem-to-map", loc));
  }
}
