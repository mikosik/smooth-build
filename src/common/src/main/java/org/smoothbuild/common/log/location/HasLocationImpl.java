package org.smoothbuild.common.log.location;

import static java.util.Objects.requireNonNull;

public class HasLocationImpl implements HasLocation {
  private final Location location;

  public HasLocationImpl(Location location) {
    this.location = requireNonNull(location);
  }

  @Override
  public Location location() {
    return location;
  }
}
