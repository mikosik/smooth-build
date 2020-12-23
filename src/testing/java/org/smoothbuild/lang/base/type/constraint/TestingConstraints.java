package org.smoothbuild.lang.base.type.constraint;

import static org.smoothbuild.lang.base.type.constraint.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.constraint.Side.LOWER;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.TypeVariable;

public class TestingConstraints {
  public static Constraints constraints(TypeVariable var, Type lowerBound) {
    return Constraints.empty().addBounds(var, oneSideBound(LOWER, lowerBound));
  }
}
