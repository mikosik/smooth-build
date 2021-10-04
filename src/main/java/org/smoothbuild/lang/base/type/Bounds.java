package org.smoothbuild.lang.base.type;

public record Bounds(Type lower, Type upper) {
  public Type get(Sides.Side side) {
    return side.dispatch(() -> lower, () -> upper);
  }
}
