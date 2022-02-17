package org.smoothbuild.lang.type.api;

public record Bounds<T extends Type>(T lower, T upper) {
  public T get(Sides.Side<T> side) {
    return switch (side) {
      case Sides.Lower l -> lower;
      case Sides.Upper u -> upper;
    };
  }
}
