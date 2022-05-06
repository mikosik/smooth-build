package org.smoothbuild.lang.type.impl;

import org.smoothbuild.lang.type.api.Sides;

public record BoundedS(VarS var, Sides<TypeS> bounds) {
  @Override
  public String toString() {
    return var.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}
