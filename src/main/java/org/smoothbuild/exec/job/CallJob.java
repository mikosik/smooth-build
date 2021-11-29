package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.concat;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.FuncH;
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

  public CallJob(TypeH type, Job referencable, ImmutableList<Job> args, Loc loc,
      BoundsMap<TypeH> vars, IndexedScope<Job> scope, JobCreator jobCreator) {
    super(type, concat(referencable, args), new NalImpl("building-evaluation", loc));
    this.args = args;
    this.vars = vars;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    var result = new PromisedValue<ValueH>();
    funcJob()
        .schedule(worker)
        .addConsumer(valueH -> onFuncJobCompleted(valueH, worker, result));
    return result;
  }

  private void onFuncJobCompleted(ValueH valueH, Worker worker, Consumer<ValueH> result) {
    var funcH = (FuncH) valueH;
    jobCreator.evaluateFuncEagerJob(scope, vars, type(), funcH, args, loc())
        .schedule(worker)
        .addConsumer(result);
  }

  private Job funcJob() {
    return dependencies().get(0);
  }
}
