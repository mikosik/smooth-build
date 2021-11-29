package org.smoothbuild.lang.base.type.api;

public record Bounded<T extends Type>(Var var, Bounds<T> bounds) {
  @Override
  public String toString() {
    return var.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}
