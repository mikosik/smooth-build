package org.smoothbuild.lang.base.type.api;

public record Bounds<T extends Type>(T lower, T upper) {
  public T get(Sides.Side<T> side) {
    return side.dispatch(() -> lower, () -> upper);
  }
}
