package org.smoothbuild.bytecode.type;

import org.smoothbuild.bytecode.type.val.AnyTB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.bytecode.type.val.VarSetB;
import org.smoothbuild.util.type.Side;
import org.smoothbuild.util.type.Sides;

import com.google.common.collect.ImmutableList;

public interface TypeBF {
  public AnyTB any();

  public NothingTB nothing();

  public ArrayTB array(TypeB elemType);

  public FuncTB func(VarSetB tParams, TypeB resT, ImmutableList<TypeB> paramTs);

  public TupleTB tuple(ImmutableList<TypeB> items);

  public VarB var(String name);

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
