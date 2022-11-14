package org.smoothbuild.compile.lang.base;

import java.util.Optional;

import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Tonal = Type + optional-name + Loc
 */
public class Tonal extends Onal {
  private final TypeS type;

  public Tonal(TypeS type, Optional<String> nameO, Loc loc) {
    super(nameO, loc);
    this.type = type;
  }

  public TypeS type() {
    return type;
  }
}
