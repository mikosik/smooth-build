package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.parallel.ParallelJobExecutor.Worker;
import org.smoothbuild.lang.base.define.Named;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

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
  public Feeder<Val> schedule(Worker worker) {
    FeedingConsumer<Val> result = new FeedingConsumer<>();
    result.accept(val);
    return result;
  }
}
