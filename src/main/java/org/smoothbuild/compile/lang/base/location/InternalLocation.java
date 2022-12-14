package org.smoothbuild.compile.lang.base.location;

public final class InternalLocation implements Location {
  public static final InternalLocation INSTANCE = new InternalLocation();

  private InternalLocation() {}

  @Override
  public String toString() {
    return "internal";
  }
}
