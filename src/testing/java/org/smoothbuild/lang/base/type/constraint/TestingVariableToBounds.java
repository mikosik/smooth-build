package org.smoothbuild.lang.base.type.constraint;

import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;

import org.smoothbuild.lang.base.type.Side;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.TypeVariable;
import org.smoothbuild.lang.base.type.VariableToBounds;

public class TestingVariableToBounds {
  public static VariableToBounds vtb(TypeVariable var, Side side, Type bound) {
    return VariableToBounds.empty().addBounds(var, oneSideBound(side, bound));
  }

  public static VariableToBounds vtb(
      TypeVariable var1, Side side1, Type bound1,
      TypeVariable var2, Side side2, Type bound2) {
    return vtb(var1, side1, bound1)
        .addBounds(var2, oneSideBound(side2, bound2));
  }
}
