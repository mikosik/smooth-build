package org.smoothbuild.bytecode.type;

import java.util.List;

import org.smoothbuild.bytecode.type.cnst.AnyTB;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.NothingTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.util.type.Bounds;
import org.smoothbuild.util.type.Side;

public interface TypeFB {
  public AnyTB any();

  public NothingTB nothing();

  public ArrayTB array(TypeB elemType);

  public FuncTB func(TypeB resT, List<? extends TypeB> paramTs);

  public TupleTB tuple(List<? extends TypeB> items);

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
