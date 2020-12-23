package org.smoothbuild.lang.base.type.constraint;

import static org.smoothbuild.lang.base.type.Types.any;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.constraint.Side.LOWER;
import static org.smoothbuild.lang.base.type.constraint.Side.UPPER;

import org.smoothbuild.lang.base.type.Type;

public record Bounds(Type lower, Type upper) {
  public static Bounds oneSideBound(Side side, Type type) {
    return switch (side) {
      case UPPER -> new Bounds(nothing(), type);
      case LOWER -> new Bounds(type, any());
    };
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
