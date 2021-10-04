package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.type.Types.anyT;
import static org.smoothbuild.lang.base.type.Types.nothingT;

public record Bounds(Type lower, Type upper) {
  public static Bounds oneSideBound(Sides.Side side, Type type) {
    return side.dispatch(
        () -> new Bounds(type, anyT()),
        () -> new Bounds(nothingT(), type)
    );
  }

  public Bounds mergeWith(Bounds bounds) {
    return new Bounds(
        lower.mergeWith(bounds.lower, Types.upper()),
        upper.mergeWith(bounds.upper, Types.lower()));
  }

  public Type get(Sides.Side side) {
    return side.dispatch(
        () -> lower,
        () -> upper);
  }

  public boolean areConsistent() {
    return get(Types.upper()).isAssignableFrom(get(Types.lower()));
  }
}
