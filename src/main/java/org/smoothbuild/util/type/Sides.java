package org.smoothbuild.util.type;

public record Sides<T>(T lower, T upper) {
  public T get(Side side) {
    return switch (side) {
      case LOWER -> lower;
      case UPPER -> upper;
    };
  }

  public Sides<T> with(Side side, T newBound) {
    return switch (side) {
      case LOWER -> new Sides<>(newBound, upper);
      case UPPER -> new Sides<>(lower, newBound);
    };
  }
}
