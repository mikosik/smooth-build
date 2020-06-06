package org.smoothbuild.exec.task.base;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.exec.comp.MaybeOutput;

public record Computed(MaybeOutput maybeOutput, ResultSource resultSource) {

  public Computed {
    this.maybeOutput = checkNotNull(maybeOutput);
    this.resultSource = checkNotNull(resultSource);
  }

  public MaybeOutput computed() {
    return maybeOutput;
  }
}
