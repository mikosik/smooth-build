package org.smoothbuild.lang.base.type.api;

public record Bounded(Variable variable, Bounds bounds) {
  public String toFormattedString() {
    return variable.name() + ": (" + bounds.lower().name() + ", " + bounds.upper().name() + ")";
  }
}
