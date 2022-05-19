package org.smoothbuild.util.type;

public record Bounds<T>(T lower, T upper) {
  public T get(Side side) {
    return switch (side) {
      case LOWER -> lower;
      case UPPER -> upper;
    };
  }

  public Bounds<T> with(Side side, T newBound) {
    return switch (side) {
      case LOWER -> new Bounds<>(newBound, upper);
      case UPPER -> new Bounds<>(lower, newBound);
    };
  }
}
