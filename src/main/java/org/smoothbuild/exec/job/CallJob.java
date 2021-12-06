package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.concat;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.val.FuncH;
import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Loc;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

import com.google.common.collect.ImmutableList;

public class CallJob extends AbstractJob {
  private final ImmutableList<Job> args;
  private final BoundsMap<TypeH> vars;
  private final IndexedScope<Job> scope;
  private final JobCreator jobCreator;

  public CallJob(TypeH type, Job called, ImmutableList<Job> args, Loc loc,
      BoundsMap<TypeH> vars, IndexedScope<Job> scope, JobCreator jobCreator) {
    super(type, concat(called, args), new NalImpl("building-evaluation", loc));
    this.args = args;
    this.vars = vars;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValH> schedule(Worker worker) {
    var result = new PromisedValue<ValH>();
    funcJob()
        .schedule(worker)
        .addConsumer(valueH -> onFuncJobCompleted(valueH, worker, result));
    return result;
  }

  private void onFuncJobCompleted(ValH val, Worker worker, Consumer<ValH> res) {
    var funcH = (FuncH) val;
    jobCreator.callFuncEagerJob(scope, vars, type(), funcH, args, loc())
        .schedule(worker)
        .addConsumer(res);
  }

  private Job funcJob() {
    return deps().get(0);
  }
}
