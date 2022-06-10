package org.smoothbuild.lang.type;

import org.smoothbuild.util.type.Bounds;

public record BoundedS(VarS var, Bounds<MonoTS> bounds) {
  @Override
  public String toString() {
    return var.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}
