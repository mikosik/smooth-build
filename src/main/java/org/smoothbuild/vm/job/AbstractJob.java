package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public abstract class AbstractJob implements Job {
  private final TypeB type;
  private final Loc loc;
  private volatile Promise<ValB> promise;

  public AbstractJob(TypeB type, Loc loc) {
    this.type = type;
    this.loc = loc;
  }

  @Override
  public TypeB type() {
    return type;
  }

  @Override
  public Loc loc() {
    return loc;
  }

  @Override
  public Promise<ValB> schedule(Worker worker) {
    // Double-checked locking.
    Promise<ValB> result = promise;
    if (result != null) {
      return result;
    }
    synchronized (this) {
      result = promise;
      if (result == null) {
        promise = result = scheduleImpl(worker);
      }
      return result;
    }
  }

  protected abstract Promise<ValB> scheduleImpl(Worker worker);
}
