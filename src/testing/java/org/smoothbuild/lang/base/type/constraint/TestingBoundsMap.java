package org.smoothbuild.lang.base.type.constraint;

import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.BoundsMap.boundsMap;
import static org.smoothbuild.util.Lists.list;

import org.smoothbuild.lang.base.type.Bounded;
import org.smoothbuild.lang.base.type.BoundsMap;
import org.smoothbuild.lang.base.type.Sides.Side;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.Variable;

public class TestingBoundsMap {
  public static BoundsMap bm() {
    return boundsMap();
  }

  public static BoundsMap bm(Variable var, Side side, Type bound) {
    return boundsMap(new Bounded(var, oneSideBound(side, bound)));
  }

  public static BoundsMap bm(
      Variable var1, Side side1, Type bound1,
      Variable var2, Side side2, Type bound2) {
    return bm(var1, side1, bound1)
        .mergeWith(list(new Bounded(var2, oneSideBound(side2, bound2))));
  }

  public static BoundsMap bm(
      Variable var1, Side side1, Type bound1,
      Variable var2, Side side2, Type bound2,
      Variable var3, Side side3, Type bound3) {
    return bm(var1, side1, bound1)
        .mergeWith(list(
            new Bounded(var2, oneSideBound(side2, bound2)),
            new Bounded(var3, oneSideBound(side3, bound3))
        ));
  }
}
