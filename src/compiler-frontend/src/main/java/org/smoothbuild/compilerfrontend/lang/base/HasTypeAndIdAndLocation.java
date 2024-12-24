package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;
import org.smoothbuild.compilerfrontend.lang.type.SSchema;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.lang.type.SVarSet;

public abstract class HasTypeAndIdAndLocation implements HasIdAndLocation, HasLocation {
  private final SType type;
  private final Id id;
  private final Location location;

  public HasTypeAndIdAndLocation(SType type, Id id, Location location) {
    this.type = requireNonNull(type);
    this.location = location;
    this.id = id;
  }

  public SType type() {
    return type;
  }

  public SSchema schema() {
    return new SSchema(SVarSet.varSetS(), type);
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
