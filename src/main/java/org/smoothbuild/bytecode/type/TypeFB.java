package org.smoothbuild.bytecode.type;

import java.util.Set;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.BoundedB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.bytecode.type.val.VarBoundsB;
import org.smoothbuild.bytecode.type.val.VarSetB;
import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.TypeF;
import org.smoothbuild.lang.type.api.Var;
import org.smoothbuild.lang.type.api.VarBounds;
import org.smoothbuild.lang.type.api.VarSet;

import com.google.common.collect.ImmutableList;
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

  @Override
  public default VarSetB varSet(Set<TypeB> elements) {
    return new VarSetB((Set<VarB>)(Object) elements);
  }

  @Override
  public ArrayTB array(TypeB elemType);

  @Override
  public FuncTB func(VarSet<TypeB> tParams, TypeB resT, ImmutableList<TypeB> paramTs);

  @Override
  public TupleTB tuple(ImmutableList<TypeB> items);

}
