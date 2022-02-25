package org.smoothbuild.lang.type.api;

public enum Side {
  UPPER,
  LOWER;

  public Side other() {
    return this == LOWER ? UPPER : LOWER;
  }
}
