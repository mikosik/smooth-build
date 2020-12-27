package org.smoothbuild.lang.base.type.constraint;

import static org.smoothbuild.lang.base.type.Bounds.oneSideBound;
import static org.smoothbuild.lang.base.type.Side.LOWER;

import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.base.type.TypeVariable;
import org.smoothbuild.lang.base.type.VariableToBounds;

public class TestingConstraints {
  public static VariableToBounds constraints(TypeVariable var, Type lowerBound) {
    return VariableToBounds.empty().addBounds(var, oneSideBound(LOWER, lowerBound));
  }
}
