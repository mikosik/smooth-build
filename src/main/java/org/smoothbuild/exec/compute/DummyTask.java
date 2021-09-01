package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

/**
 * Subclasses of this class must be immutable.
 */
public class DummyTask extends AbstractTask {
  private final Val val;

  public DummyTask(TaskKind kind, Type type, String name, Val val, Location location) {
    super(kind, type, name, list(), location);
    this.val = val;
  }

  @Override
  public Feeder<Val> compute(Worker worker) {
    FeedingConsumer<Val> result = new FeedingConsumer<>();
    result.accept(val);
    return result;
  }
}
