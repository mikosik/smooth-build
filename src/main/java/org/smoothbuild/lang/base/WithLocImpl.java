package org.smoothbuild.lang.base;

public class WithLocImpl implements WithLoc {
  private final Loc loc;

  public WithLocImpl(Loc loc) {
    this.loc = loc;
  }

  @Override
  public Loc loc() {
    return loc;
  }
}
