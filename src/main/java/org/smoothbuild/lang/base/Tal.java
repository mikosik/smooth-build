package org.smoothbuild.lang.base;

import org.smoothbuild.lang.type.MonoTS;

public class Tal {
  private final MonoTS type;
  private final Loc loc;

  public Tal(MonoTS type, Loc loc) {
    this.type = type;
    this.loc = loc;
  }

  public MonoTS type() {
    return type;
  }

  public Loc loc() {
    return loc;
  }
}
