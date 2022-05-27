package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

/**
 * Name and loc.
 */
public class NalImpl implements Nal {
  private final String name;
  private final Loc loc;

  public NalImpl(Nal nal) {
    this(nal.name(), nal.loc());
  }

  public NalImpl(String name, Loc loc) {
    this.name = requireNonNull(name);
    this.loc = requireNonNull(loc);
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public Loc loc() {
    return loc;
  }
}
