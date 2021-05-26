package org.smoothbuild.exec.compute;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.exec.base.MaybeOutput;

public record Computed(MaybeOutput maybeOutput, ResultSource resultSource) {

  public Computed(MaybeOutput maybeOutput, ResultSource resultSource) {
    this.maybeOutput = requireNonNull(maybeOutput);
    this.resultSource = requireNonNull(resultSource);
  }

  public MaybeOutput computed() {
    return maybeOutput;
  }
}
