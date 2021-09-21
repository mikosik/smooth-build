package org.smoothbuild.exec.job;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Named;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;

/**
 * Subclasses of this class must be immutable.
 */
public class DummyJob extends AbstractJob {
  private final Val val;

  public DummyJob(Type type, Val val, Named named) {
    super(type, list(), named);
    this.val = val;
  }

  @Override
  public Promise<Val> schedule(Worker worker) {
    PromisedValue<Val> result = new PromisedValue<>();
    result.accept(val);
    return result;
  }
}
