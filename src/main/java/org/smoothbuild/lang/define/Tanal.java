package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.type.TypeS;

/**
 * TANAL = TypeS and name and location.
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
