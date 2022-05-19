package org.smoothbuild.bytecode.type;

import java.util.List;

import org.smoothbuild.bytecode.type.val.AnyTB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.FuncTB;
import org.smoothbuild.bytecode.type.val.NothingTB;
import org.smoothbuild.bytecode.type.val.TupleTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.bytecode.type.val.VarB;
import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;

public interface TypeBF {
  public AnyTB any();

  public NothingTB nothing();

  public ArrayTB array(TypeB elemType);

  public FuncTB func(TypeB resT, List<? extends TypeB> paramTs);

  public TupleTB tuple(List<? extends TypeB> items);

  public VarB var(String name);

  public default TypeB edge(Side side) {
    return switch (side) {
      case LOWER -> nothing();
      case UPPER -> any();
    };
  }

  public default Bounds<TypeB> oneSideBound(Side side, TypeB type) {
    return switch (side) {
      case LOWER -> new Bounds<>(type, any());
      case UPPER -> new Bounds<>(nothing(), type);
    };
  }
}
