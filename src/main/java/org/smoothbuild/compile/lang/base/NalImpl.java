package org.smoothbuild.compile.lang.base;

import static java.util.Objects.requireNonNull;

/**
 * Default Nal implementation.
 */
public class NalImpl implements Nal {
  private final String name;
  private final Loc loc;

  public NalImpl(String name, Loc loc) {
    this.name = requireNonNull(name);
    this.loc = loc;
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
