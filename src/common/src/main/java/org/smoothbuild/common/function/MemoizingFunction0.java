package org.smoothbuild.common.function;

import static java.util.Objects.requireNonNull;

class MemoizingFunction0<R, T extends Throwable> implements Function0<R, T> {
  private final Function0<R, T> function0;
  private volatile boolean computed = false;
  private R result;

  MemoizingFunction0(Function0<R, T> function0) {
    this.function0 = requireNonNull(function0);
  }

  @Override
  public R apply() throws T {
    // Changes to non-volatile field `result` are safely shared between threads by piggybacking on
    // write/reads of `computed` field. This is possible because every write to a volatile field
    // happens-before every subsequent read of that same field.
    if (!computed) {
      synchronized (this) {
        if (!computed) {
          R localResult = function0.apply();
          this.result = localResult;
          this.computed = true;
          return localResult;
        }
      }
    }
    return result;
  }
}
