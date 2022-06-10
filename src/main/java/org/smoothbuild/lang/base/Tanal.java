package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.type.MonoTS;
/**
 * TANAL = type and name and location.
 */
public abstract class Tanal extends NalImpl {
  private final MonoTS type;

  public Tanal(MonoTS type, String name, Loc loc) {
    super(name, loc);
    this.type = requireNonNull(type);
  }

  public MonoTS type() {
    return type;
  }
}
