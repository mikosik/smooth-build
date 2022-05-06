package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.util.type.Sides;

public record BoundedB(VarB var, Sides<TypeB> bounds) {
  @Override
  public String toString() {
    return var.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}

