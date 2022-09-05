package org.smoothbuild.compile.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.compile.lang.type.TypeS;

/**
 * TANAL = Type and Name and Loc.
 */
public abstract class Tanal extends NalImpl {
  private final TypeS type;

  public Tanal(TypeS type, String name, Loc loc) {
    super(name, loc);
    this.type = requireNonNull(type);
  }

  public TypeS type() {
    return type;
  }
}
