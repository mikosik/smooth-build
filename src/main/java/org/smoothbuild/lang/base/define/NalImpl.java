package org.smoothbuild.lang.base.define;

import static java.util.Objects.requireNonNull;

/**
 * Name and location.
 */
public class NalImpl implements Nal {
  private final String name;
  private final Location location;

  public NalImpl(Nal nal) {
    this(nal.name(), nal.location());
  }

  public NalImpl(String name, Location location) {
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
