package org.smoothbuild.lang.base.type.constraint;

import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;

import org.smoothbuild.lang.base.type.BoundedVariables;
import org.smoothbuild.lang.base.type.Side;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Variable;

public class TestingBoundedVariables {
  public static BoundedVariables bv() {
    return BoundedVariables.empty();
  }

  public static BoundedVariables bv(Variable var, Side side, Type bound) {
    return BoundedVariables.empty().addBounds(var, oneSideBound(side, bound));
  }

  public static BoundedVariables bv(
      Variable var1, Side side1, Type bound1,
      Variable var2, Side side2, Type bound2) {
    return bv(var1, side1, bound1)
        .addBounds(var2, oneSideBound(side2, bound2));
  }

  public static BoundedVariables bv(
      Variable var1, Side side1, Type bound1,
      Variable var2, Side side2, Type bound2,
      Variable var3, Side side3, Type bound3) {
    return bv(var1, side1, bound1, var2, side2, bound2)
        .addBounds(var3, oneSideBound(side3, bound3));
  }
}
