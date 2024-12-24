package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;

public class HasIdAndLocationImpl implements HasIdAndLocation, HasLocation {
  private final Id id;
  private final Location location;

  public HasIdAndLocationImpl(Id id, Location location) {
    this.id = requireNonNull(id);
    this.location = location;
  }

  @Override
  public Id id() {
    return id;
  }

  @Override
  public Location location() {
    return location;
  }
}
