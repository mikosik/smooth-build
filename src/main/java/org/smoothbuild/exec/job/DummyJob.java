package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.val.ValH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

public class DummyJob extends AbstractJob {
  private final ValH val;

  public DummyJob(TypeH type, ValH val, Nal nal) {
    super(type, list(), nal);
    this.val = val;
  }

  @Override
  public Promise<ValH> schedule(Worker worker) {
    PromisedValue<ValH> result = new PromisedValue<>();
    result.accept(val);
    return result;
  }
}
