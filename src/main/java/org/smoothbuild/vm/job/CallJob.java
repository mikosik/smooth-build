package org.smoothbuild.vm.job;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.bytecode.obj.val.FuncB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.bytecode.type.val.VarBoundsB;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

import com.google.common.collect.ImmutableList;

public class CallJob extends AbstractJob {
  private final Job callableJ;
  private final ImmutableList<Job> argJs;
  private final VarBoundsB vars;
  private final List<Job> params;
  private final JobCreator jobCreator;

  public CallJob(TypeB type, Job callableJ, ImmutableList<Job> argJs, Loc loc,
      VarBoundsB vars, List<Job> params, JobCreator jobCreator) {
    super(type, loc);
    this.callableJ = callableJ;
    this.argJs = argJs;
    this.vars = vars;
    this.params = params;
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
    jobCreator.callFuncEagerJob(type(), funcH, argJs, loc(), params, vars)
        .schedule(worker)
        .addConsumer(res);
  }

  private Job funcJob() {
    return callableJ;
  }
}
