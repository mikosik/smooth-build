package org.smoothbuild.exec.job;

import static org.smoothbuild.util.Lists.list;

import java.util.function.Consumer;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.exec.base.LambdaRec;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.exec.plan.JobCreator;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.util.Scope;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class DefaultArgumentJob extends AbstractJob {
  private final int index;
  private final Scope<Job> scope;
  private final JobCreator jobCreator;

  public DefaultArgumentJob(Type type, String name, Job function, int index,
      Location location, Scope<Job> scope, JobCreator jobCreator) {
    super(type, list(function), new Nal("building: " + name, location));
    this.index = index;
    this.scope = scope;
    this.jobCreator = jobCreator;
  }

  @Override
  public Promise<Val> schedule(Worker worker) {
    PromisedValue<Val> result = new PromisedValue<>();
    dependencies().get(0)
        .schedule(worker)
        .addConsumer(obj -> onCompleted(obj, worker, result));
    return result;
  }

  private void onCompleted(Val val, Worker worker, Consumer<Val> result) {
    String functionName = LambdaRec.name(((Struc_) val)).jValue();
    jobCreator.defaultArgumentEagerJob(scope, functionName, index)
        .schedule(worker)
        .addConsumer(result);
  }
}
