package org.smoothbuild.compilerfrontend.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.common.log.location.Location;

/**
 * Default Ial implementation.
 */
public class IalImpl implements Ial {
  private final Id id;
  private final Location location;

  public IalImpl(Id id, Location location) {
    this.id = requireNonNull(id);
    this.location = requireNonNull(location);
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
