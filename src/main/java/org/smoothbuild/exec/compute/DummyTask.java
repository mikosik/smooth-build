package org.smoothbuild.exec.compute;

import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.FeedingConsumer;

/**
 * Subclasses of this class must be immutable.
 */
public class DummyTask extends AbstractTask {
  private final Obj obj;

  public DummyTask(TaskKind kind, Type type, String name, Obj obj, Location location) {
    super(kind, type, name, list(), location);
    this.obj = obj;
  }

  @Override
  public Feeder<Obj> startComputation(Worker worker) {
    FeedingConsumer<Obj> result = new FeedingConsumer<>();
    result.accept(obj);
    return result;
  }
}
