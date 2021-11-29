package org.smoothbuild.exec.job;

import static org.smoothbuild.util.collect.Lists.list;

import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Nal;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

/**
 * Subclasses of this class must be immutable.
 */
public class DummyJob extends AbstractJob {
  private final ValueH val;

  public DummyJob(TypeH type, ValueH val, Nal nal) {
    super(type, list(), nal);
    this.val = val;
  }

  @Override
  public Promise<ValueH> schedule(Worker worker) {
    PromisedValue<ValueH> result = new PromisedValue<>();
    result.accept(val);
    return result;
  }
}
