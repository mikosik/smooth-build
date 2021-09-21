package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;

/**
 * Name and location.
 */
public class Nal implements Named {
  private final String name;
  private final Location location;

  public Nal(Named named) {
    this(named.name(), named.location());
  }

  public Nal(String name, Location location) {
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
