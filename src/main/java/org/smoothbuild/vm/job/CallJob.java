package org.smoothbuild.vm.job;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.expr.val.FuncB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

import com.google.common.collect.ImmutableList;

public class CallJob extends AbstractJob {
  private final Job funcJ;
  private final ImmutableList<Job> argJs;
  private final JobCreator jobCreator;

  public CallJob(TypeB type, Job funcJ, ImmutableList<Job> argJs, Loc loc, JobCreator jobCreator) {
    super(type, loc);
    this.funcJ = funcJ;
    this.argJs = argJs;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValB> scheduleImpl(Worker worker) {
    var result = new PromisedValue<ValB>();
    funcJ()
        .schedule(worker)
        .addConsumer(valB -> onFuncJobCompleted(valB, worker, result));
    return result;
  }

  private void onFuncJobCompleted(ValB valB, Worker worker, Consumer<ValB> res) {
    var funcB = (FuncB) valB;
    jobCreator.callFuncEagerJob(funcB, argJs, loc())
        .schedule(worker)
        .addConsumer(res);
  }

  private Job funcJ() {
    return funcJ;
  }
}
