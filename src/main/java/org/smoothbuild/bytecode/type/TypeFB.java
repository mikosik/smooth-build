package org.smoothbuild.bytecode.type;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.BoundedB;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.TypeF;
import org.smoothbuild.lang.type.api.Var;

public interface TypeFB extends TypeF<TypeB> {
  @Override
  public default BoundedB bounded(Var var, Sides<TypeB> sides) {
    return new BoundedB(var, sides);
  }
}
