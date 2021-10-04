package org.smoothbuild.lang.base.type;

public record Bounded(Variable variable, Bounds bounds) {
  public String toFormattedString() {
    return variable.name() + ": (" + bounds.lower().name() + ", " + bounds.upper().name() + ")";
  }
}
