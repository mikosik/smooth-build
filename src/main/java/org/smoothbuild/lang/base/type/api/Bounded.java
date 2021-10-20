package org.smoothbuild.lang.base.type.api;

public record Bounded(Variable variable, Bounds bounds) {
  @Override
  public String toString() {
    return variable.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}
