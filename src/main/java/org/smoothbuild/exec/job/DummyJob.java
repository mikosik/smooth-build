package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.val.ValB;
import org.smoothbuild.db.object.type.base.TypeB;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class DummyJob extends AbstractJob {
  private final ValB val;

  public DummyJob(TypeB type, ValB val, Nal nal) {
    super(type, list(), nal);
    this.val = val;
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    PromisedValue<ValB> result = new PromisedValue<>();
    result.accept(val);
    return result;
  }
}
