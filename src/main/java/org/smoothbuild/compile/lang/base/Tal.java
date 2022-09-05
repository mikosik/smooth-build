package org.smoothbuild.compile.lang.base;

import org.smoothbuild.compile.lang.type.TypeS;

/**
 * TAL = Type and Loc.
 */
public class Tal extends WithLocImpl {
  private final TypeS type;

  public Tal(TypeS type, Loc loc) {
    super(loc);
    this.type = type;
  }

  public TypeS type() {
    return type;
  }
}
