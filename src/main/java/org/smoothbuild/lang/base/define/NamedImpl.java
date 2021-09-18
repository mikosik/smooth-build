package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;

public class NamedImpl implements Named {
  private final String name;
  private final Location location;

  public NamedImpl(String name, Location location) {
    this.name = requireNonNull(name);
    this.location = requireNonNull(location);
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
