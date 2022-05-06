package org.smoothbuild.util.type;

public enum Side {
  UPPER,
  LOWER;

  public Side other() {
    return this == LOWER ? UPPER : LOWER;
  }

  public <T> T of(T lower, T upper) {
    return switch (this) {
      case LOWER -> lower;
      case UPPER -> upper;
    };
  }
}
