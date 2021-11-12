package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.concat;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.exec.base.FunctionStruct;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.NalImpl;
import org.smoothbuild.lang.base.type.api.BoundsMap;
import org.smoothbuild.lang.base.type.impl.TypeS;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.collect.Labeled;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class ApplyJob extends AbstractJob {
  private final List<Job> arguments;
  private final BoundsMap<TypeS> variables;
  private final Scope<Labeled<Job>> scope;
  private final JobCreator jobCreator;

  public ApplyJob(TypeS type, Job referencable, List<Job> arguments, Location location,
      BoundsMap<TypeS> variables, Scope<Labeled<Job>> scope, JobCreator jobCreator) {
    super(type, concat(referencable, arguments), new NalImpl("building-evaluation", location));
    this.arguments = arguments;
    this.variables = variables;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    PromisedValue<ValueH> result = new PromisedValue<>();
    functionJob()
        .schedule(worker)
        .addConsumer(obj -> onFunctionJobCompleted(obj, worker, result));
    return result;
  }

  private void onFunctionJobCompleted(ValueH val, Worker worker, Consumer<ValueH> result) {
    String name = FunctionStruct.name(((TupleH) val)).jValue();
    jobCreator.evaluateFunctionEagerJob(scope, variables, type(), name, arguments, location())
        .schedule(worker)
        .addConsumer(result);
  }

  private Job functionJob() {
    return dependencies().get(0);
  }
}
