package org.smoothbuild.compile.lang.base.location;

public final class UnknownLocation implements Location {
  public static final UnknownLocation INSTANCE = new UnknownLocation();

  private UnknownLocation() {}

  @Override
  public String toString() {
    return "???";
  }
}
