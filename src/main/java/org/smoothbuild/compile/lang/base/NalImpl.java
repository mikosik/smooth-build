package org.smoothbuild.compile.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.compile.lang.base.location.Location;

/**
 * Default Nal implementation.
 */
public class NalImpl implements Nal {
  private final String name;
  private final Location location;

  public NalImpl(String name, Location location) {
    this.name = requireNonNull(name);
    this.location = location;
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Location location() {
    return location;
  }
}
