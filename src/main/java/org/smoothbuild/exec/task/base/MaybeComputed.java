package org.smoothbuild.exec.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.exec.comp.MaybeOutput;

/**
 * This class is immutable.
 */
public class MaybeComputed {
  private final MaybeOutput maybeOutput;
  private final boolean isFromCache;
  private final Throwable throwable;

  public MaybeComputed(MaybeOutput mabyeOutput, boolean isFromCache) {
    this.maybeOutput = checkNotNull(mabyeOutput);
    this.isFromCache = isFromCache;
    this.throwable = null;
  }

  public MaybeComputed(Throwable throwable) {
    this.maybeOutput = null;
    this.isFromCache = false;
    this.throwable = checkNotNull(throwable);
  }

  public boolean hasComputed() {
    return maybeOutput != null;
  }

  public MaybeOutput computed() {
    return maybeOutput;
  }

  public boolean isFromCache() {
    return isFromCache;
  }

  public Throwable throwable() {
    return throwable;
  }
}
