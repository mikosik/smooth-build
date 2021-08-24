package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Side.LOWER;
import static org.smoothbuild.lang.base.type.Side.UPPER;
import static org.smoothbuild.lang.base.type.Types.anyT;
import static org.smoothbuild.lang.base.type.Types.nothingT;

public record Bounds(Type lower, Type upper) {
  public static Bounds oneSideBound(Side side, Type type) {
    return switch (side) {
      case UPPER -> new Bounds(nothingT(), type);
      case LOWER -> new Bounds(type, anyT());
    };
  }

  public static Bounds unbounded() {
    return new Bounds(nothingT(), anyT());
  }

  public Bounds mergeWith(Bounds bounds) {
    return new Bounds(
        lower.mergeWith(bounds.lower, UPPER),
        upper.mergeWith(bounds.upper, LOWER));
  }

  public Type get(Side side) {
    return switch (side) {
      case UPPER -> upper;
      case LOWER -> lower;
    };
  }

  public boolean areConsistent() {
    return get(UPPER).isAssignableFrom(get(LOWER));
  }
}
