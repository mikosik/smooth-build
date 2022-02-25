package org.smoothbuild.lang.type.api;

public record Bounds<T>(T lower, T upper) {
  public T get(Side side) {
    return switch (side) {
      case LOWER -> lower;
      case UPPER -> upper;
    };
  }
}
