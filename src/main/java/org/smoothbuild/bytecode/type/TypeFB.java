package org.smoothbuild.bytecode.type;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.BoundedB;
import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.TypeF;
import org.smoothbuild.lang.type.api.Var;
import org.smoothbuild.lang.type.api.VarBounds;
import org.smoothbuild.bytecode.type.val.VarBoundsB;

import com.google.common.collect.ImmutableMap;

public interface TypeFB extends TypeF<TypeB> {
  @Override
  public default BoundedB bounded(Var var, Sides<TypeB> sides) {
    return new BoundedB(var, sides);
  }

  @Override
  public default VarBounds<TypeB> varBounds(ImmutableMap<Var, Bounded<TypeB>> map) {
    return new VarBoundsB(map);
  }
}
