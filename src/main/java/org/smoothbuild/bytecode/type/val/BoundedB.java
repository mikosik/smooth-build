package org.smoothbuild.bytecode.type.val;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.Var;

public record BoundedB(Var var, Sides<TypeB> bounds) implements Bounded<TypeB> {
  @Override
  public String toString() {
    return var.name() + ":<" + bounds.lower().name() + "," + bounds.upper().name() + ">";
  }
}

