package org.smoothbuild.lang.type;

import org.smoothbuild.util.type.Sides;

public record BoundedS(VarS var, Sides<TypeS> bounds) {
  @Override
  public String toString() {
    return var.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}
