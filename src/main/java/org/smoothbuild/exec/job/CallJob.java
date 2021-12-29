package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.concat;

import java.util.function.Consumer;

import org.smoothbuild.db.bytecode.obj.val.FuncB;
import org.smoothbuild.db.bytecode.obj.val.ValB;
import org.smoothbuild.db.bytecode.type.base.TypeB;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.type.api.VarBounds;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

import com.google.common.collect.ImmutableList;

public class CallJob extends AbstractJob {
  private final ImmutableList<Job> args;
  private final VarBounds<TypeB> vars;
  private final IndexedScope<Job> scope;
  private final JobCreator jobCreator;

  public CallJob(TypeB type, Job called, ImmutableList<Job> args, Loc loc,
      VarBounds<TypeB> vars, IndexedScope<Job> scope, JobCreator jobCreator) {
    super(type, concat(called, args), new NalImpl("building-evaluation", loc));
    this.args = args;
    this.vars = vars;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    var result = new PromisedValue<ValB>();
    funcJob()
        .schedule(worker)
        .addConsumer(valueH -> onFuncJobCompleted(valueH, worker, result));
    return result;
  }

  private void onFuncJobCompleted(ValB val, Worker worker, Consumer<ValB> res) {
    var funcH = (FuncB) val;
    jobCreator.callFuncEagerJob(type(), funcH, args, loc(), scope, vars)
        .schedule(worker)
        .addConsumer(res);
  }

  private Job funcJob() {
    return deps().get(0);
  }
}
