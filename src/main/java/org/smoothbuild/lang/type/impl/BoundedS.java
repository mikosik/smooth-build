package org.smoothbuild.lang.type.impl;

import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.Var;

public record BoundedS(Var var, Sides<TypeS> bounds) implements Bounded<TypeS> {
  @Override
  public String toString() {
    return var.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}
