package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.HasLocationImpl;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.name.Id;

public class HasIdAndLocationImpl extends HasLocationImpl implements HasIdAndLocation {
  private final Id id;

  public HasIdAndLocationImpl(Id id, Location location) {
    super(location);
    this.id = requireNonNull(id);
  }

  @Override
  public Id id() {
    return id;
  }
}
