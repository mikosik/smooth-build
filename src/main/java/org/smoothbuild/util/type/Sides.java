package org.smoothbuild.util.type;

public record Sides<T>(T lower, T upper) {
  public T get(Side side) {
    return switch (side) {
      case LOWER -> lower;
      case UPPER -> upper;
    };
  }
}
