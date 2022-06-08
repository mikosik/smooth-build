package org.smoothbuild.lang.base;

public class WithLoc {
  private final Loc loc;

  public WithLoc(Loc loc) {
    this.loc = loc;
  }

  public Loc loc() {
    return loc;
  }
}
