package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.type.TypeS;

/**
 * TANAL = TypeS and name and location.
 */
public abstract class TanalImpl extends NalImpl implements Tanal {
  private final TypeS type;

  public TanalImpl(TypeS type, String name, Loc loc) {
    super(name, loc);
    this.type = requireNonNull(type);
  }

  @Override
  public TypeS type() {
    return type;
  }
}