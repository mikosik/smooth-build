package org.smoothbuild.vm.job;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.obj.cnst.FuncB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

import com.google.common.collect.ImmutableList;

public class CallJob extends AbstractJob {
  private final Job funcJ;
  private final ImmutableList<Job> argJs;
  private final List<Job> bindings;
  private final JobCreator jobCreator;

  public CallJob(TypeB type, Job funcJ, ImmutableList<Job> argJs, Loc loc,
      List<Job> bindings, JobCreator jobCreator) {
    super(type, loc);
    this.funcJ = funcJ;
    this.argJs = argJs;
    this.bindings = bindings;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<CnstB> scheduleImpl(Worker worker) {
    var result = new PromisedValue<CnstB>();
    funcJ()
        .schedule(worker)
        .addConsumer(cnstB -> onFuncJobCompleted(cnstB, worker, result));
    return result;
  }

  private void onFuncJobCompleted(CnstB cnstB, Worker worker, Consumer<CnstB> res) {
    var funcB = (FuncB) cnstB;
    jobCreator.callFuncEagerJob(funcB, argJs, loc(), bindings)
        .schedule(worker)
        .addConsumer(res);
  }

  private Job funcJ() {
    return funcJ;
  }
}
