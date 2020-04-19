package org.smoothbuild.exec.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.exec.comp.MaybeOutput;

/**
 * This class is immutable.
 */
public class Computed {
  private final MaybeOutput maybeOutput;
  private final boolean isFromCache;

  public Computed(MaybeOutput mabyeOutput, boolean isFromCache) {
    this.maybeOutput = checkNotNull(mabyeOutput);
    this.isFromCache = isFromCache;
  }

  public MaybeOutput computed() {
    return maybeOutput;
  }

  public boolean isFromCache() {
    return isFromCache;
  }
}
