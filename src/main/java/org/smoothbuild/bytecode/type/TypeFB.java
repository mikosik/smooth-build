package org.smoothbuild.bytecode.type;

import java.util.Set;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.AnyTB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.BoundedB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.bytecode.type.val.VarBoundsB;
import org.smoothbuild.bytecode.type.val.VarSetB;
import org.smoothbuild.lang.type.api.Bounded;
import org.smoothbuild.lang.type.api.Side;
import org.smoothbuild.lang.type.api.Sides;
import org.smoothbuild.lang.type.api.Var;
import org.smoothbuild.lang.type.api.VarBounds;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public interface TypeFB {
  public AnyTB any();

  public NothingTB nothing();

  public ArrayTB array(TypeB elemType);

  public FuncTB func(VarSetB tParams, TypeB resT, ImmutableList<TypeB> paramTs);

  public TupleTB tuple(ImmutableList<TypeB> items);

  public Var var(String name);

  public default BoundedB bounded(Var var, Sides<TypeB> sides) {
    return new BoundedB(var, sides);
  }

  public default VarBounds<TypeB> varBounds(ImmutableMap<Var, Bounded<TypeB>> map) {
    return new VarBoundsB(map);
  }

  public default VarSetB varSet(Set<VarB> elements) {
    return new VarSetB(elements);
  }

  public default TypeB edge(Side side) {
    return switch (side) {
      case LOWER -> nothing();
      case UPPER -> any();
    };
  }

  public default Sides<TypeB> oneSideBound(Side side, TypeB type) {
    return switch (side) {
      case LOWER -> new Sides<>(type, any());
      case UPPER -> new Sides<>(nothing(), type);
    };
  }
}
