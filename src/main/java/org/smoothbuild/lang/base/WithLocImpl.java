package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

public class WithLocImpl implements WithLoc {
  private final Loc loc;

  public WithLocImpl(Loc loc) {
    this.loc = requireNonNull(loc);
  }

  @Override
  public Loc loc() {
    return loc;
  }
}
