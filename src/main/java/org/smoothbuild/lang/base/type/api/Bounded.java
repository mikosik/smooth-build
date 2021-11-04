package org.smoothbuild.lang.base.type.api;

public record Bounded<T extends Type>(Variable variable, Bounds<T> bounds) {
  @Override
  public String toString() {
    return variable.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}
