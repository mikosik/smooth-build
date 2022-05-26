package org.smoothbuild.bytecode.type.cnst;

import org.smoothbuild.util.type.Bounds;

public record BoundedB(VarB var, Bounds<TypeB> bounds) {
  @Override
  public String toString() {
    return var.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}

