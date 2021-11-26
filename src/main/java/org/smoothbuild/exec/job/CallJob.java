package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.concat;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.FunctionH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.util.IndexedScope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

import com.google.common.collect.ImmutableList;

public class CallJob extends AbstractJob {
  private final ImmutableList<Job> args;
  private final BoundsMap<TypeHV> vars;
  private final IndexedScope<Job> scope;
  private final JobCreator jobCreator;

  public CallJob(TypeHV type, Job referencable, ImmutableList<Job> args, Location location,
      BoundsMap<TypeHV> vars, IndexedScope<Job> scope, JobCreator jobCreator) {
    super(type, concat(referencable, args), new NalImpl("building-evaluation", location));
    this.args = args;
    this.vars = vars;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    var result = new PromisedValue<ValueH>();
    functionJob()
        .schedule(worker)
        .addConsumer(valueH -> onFunctionJobCompleted(valueH, worker, result));
    return result;
  }

  private void onFunctionJobCompleted(ValueH valueH, Worker worker, Consumer<ValueH> result) {
    var functionH = (FunctionH) valueH;
    jobCreator.evaluateFunctionEagerJob(scope, vars, type(), functionH, args, location())
        .schedule(worker)
        .addConsumer(result);
  }

  private Job functionJob() {
    return dependencies().get(0);
  }
}
