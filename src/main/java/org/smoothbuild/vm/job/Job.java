package org.smoothbuild.vm.job;

import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.Promise;

public abstract class Job {
  private volatile Promise<ValB> promise;

  public final Promise<ValB> evaluate() {
    // Double-checked locking.
    Promise<ValB> result = promise;
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

  protected abstract Promise<ValB> evaluateImpl();
}
