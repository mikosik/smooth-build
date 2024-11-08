package org.smoothbuild.common.log.location;

public final class InternalLocation implements Location {
  public static final InternalLocation INSTANCE = new InternalLocation();

  private InternalLocation() {}

  @Override
  public String toString() {
    return "internal";
  }

  @Override
  public String description() {
    return "internal location";
  }
}
