package org.smoothbuild.lang.base;

import org.smoothbuild.lang.type.TypeS;

public class Tal {
  private final TypeS type;
  private final Loc loc;

  public Tal(TypeS type, Loc loc) {
    this.type = type;
    this.loc = loc;
  }

  public TypeS type() {
    return type;
  }

  public Loc loc() {
    return loc;
  }
}
