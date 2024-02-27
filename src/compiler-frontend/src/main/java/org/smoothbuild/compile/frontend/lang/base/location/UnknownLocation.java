package org.smoothbuild.compile.frontend.lang.base.location;

public final class UnknownLocation implements Location {
  public static final UnknownLocation INSTANCE = new UnknownLocation();

  private UnknownLocation() {}

  @Override
  public String description() {
    return "unknown location";
  }

  @Override
  public String toString() {
    return "???";
  }
}
