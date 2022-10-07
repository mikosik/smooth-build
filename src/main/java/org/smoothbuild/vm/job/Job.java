package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.util.concurrent.Promise;

public abstract class Job {
  private volatile Promise<InstB> promise;

  public final Promise<InstB> evaluate() {
    // Double-checked locking.
    Promise<InstB> result = promise;
    if (result != null) {
      return result;
    }
    synchronized (this) {
      result = promise;
      if (result == null) {
        promise = result = evaluateImpl();
      }
      return result;
    }
  }

  protected abstract Promise<InstB> evaluateImpl();
}
