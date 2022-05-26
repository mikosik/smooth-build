package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.obj.cnst.CnstB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.lang.define.Loc;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

public abstract class AbstractJob implements Job {
  private final TypeB type;
  private final Loc loc;
  private volatile Promise<CnstB> promise;

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
  public Promise<CnstB> schedule(Worker worker) {
    // Double-checked locking.
    Promise<CnstB> result = promise;
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

  protected abstract Promise<CnstB> scheduleImpl(Worker worker);
}
