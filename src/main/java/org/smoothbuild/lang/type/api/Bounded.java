package org.smoothbuild.lang.type.api;

public record Bounded<T extends Type>(VarT var, Sides<T> bounds) {
  @Override
  public String toString() {
    return var.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}
