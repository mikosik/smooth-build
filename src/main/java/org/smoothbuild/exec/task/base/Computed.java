package org.smoothbuild.exec.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.exec.comp.MaybeOutput;

/**
 * This class is immutable.
 */
public class Computed {
  private final MaybeOutput maybeOutput;
  private final ResultSource resultSource;

  public Computed(MaybeOutput mabyeOutput, ResultSource resultSource) {
    this.maybeOutput = checkNotNull(mabyeOutput);
    this.resultSource = resultSource;
  }

  public MaybeOutput computed() {
    return maybeOutput;
  }

  public ResultSource resultSource() {
    return resultSource;
  }
}
