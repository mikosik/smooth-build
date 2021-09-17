package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Rec;
import org.smoothbuild.exec.base.LambdaRec;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.NamedImpl;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

public class DefaultArgumentJob extends AbstractJob {
  private final int index;
  private final Scope<Job> scope;
  private final JobCreator jobCreator;

  public DefaultArgumentJob(Type type, String name, Job function, int index,
      Location location, Scope<Job> scope, JobCreator jobCreator) {
    super(type, list(function), new NamedImpl("building: " + name, location));
    this.index = index;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Feeder<Val> schedule(Worker worker) {
    FeedingConsumer<Val> result = new FeedingConsumer<>();
    dependencies().get(0)
        .schedule(worker)
        .addConsumer(obj -> onCompleted(obj, worker, result));
    return result;
  }

  private void onCompleted(Val val, Worker worker, Consumer<Val> result) {
    String functionName = LambdaRec.name(((Rec) val)).jValue();
    jobCreator.defaultArgumentEagerJob(scope, functionName, index)
        .schedule(worker)
        .addConsumer(result);
  }
}
