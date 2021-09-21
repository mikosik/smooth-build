package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.concat;

import java.util.List;
import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.exec.base.LambdaRec;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.type.BoundsMap;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

public class ApplyJob extends AbstractJob {
  private final List<Job> arguments;
  private final BoundsMap variables;
  private final Scope<Job> scope;
  private final JobCreator jobCreator;

  public ApplyJob(Type type, Job referencable, List<Job> arguments, Location location,
      BoundsMap variables, Scope<Job> scope, JobCreator jobCreator) {
    super(type, concat(referencable, arguments), new Nal("building-evaluation", location));
    this.arguments = arguments;
    this.variables = variables;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Feeder<Val> schedule(Worker worker) {
    FeedingConsumer<Val> result = new FeedingConsumer<>();
    lambdaJob()
        .schedule(worker)
        .addConsumer(obj -> onLambdaCompleted(obj, worker, result));
    return result;
  }

  private void onLambdaCompleted(Val val, Worker worker, Consumer<Val> result) {
    String name = LambdaRec.name(((Rec) val)).jValue();
    jobCreator.evaluateLambdaEagerJob(scope, variables, type(), name, arguments, location())
        .schedule(worker)
        .addConsumer(result);
  }

  private Job lambdaJob() {
    return dependencies().get(0);
  }
}
