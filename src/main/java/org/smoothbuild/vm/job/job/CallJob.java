package org.smoothbuild.vm.job.job;

import java.util.function.Consumer;

import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.type.api.VarBounds;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.job.JobCreator;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

import com.google.common.collect.ImmutableList;

public class CallJob extends AbstractJob {
  private final Job callableJ;
  private final ImmutableList<Job> argJs;
  private final VarBounds<TypeB> vars;
  private final IndexedScope<Job> scope;
  private final JobCreator jobCreator;

  public CallJob(TypeB type, Job callableJ, ImmutableList<Job> argJs, Loc loc,
      VarBounds<TypeB> vars, IndexedScope<Job> scope, JobCreator jobCreator) {
    super(type, loc);
    this.callableJ = callableJ;
    this.argJs = argJs;
    this.vars = vars;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValB> scheduleImpl(Worker worker) {
    var result = new PromisedValue<ValB>();
    funcJob()
        .schedule(worker)
        .addConsumer(valueH -> onFuncJobCompleted(valueH, worker, result));
    return result;
  }

  private void onFuncJobCompleted(ValB val, Worker worker, Consumer<ValB> res) {
    var funcH = (FuncB) val;
    jobCreator.callFuncEagerJob(funcH, argJs, loc(), scope, vars)
        .schedule(worker)
        .addConsumer(res);
  }

  private Job funcJob() {
    return callableJ;
  }
}
