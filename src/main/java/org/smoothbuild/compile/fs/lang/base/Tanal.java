package org.smoothbuild.compile.fs.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * TANAL = Type and Name and Loc.
 */
public abstract class Tanal extends NalImpl {
  private final TypeS type;

  public Tanal(TypeS type, String name, Location location) {
    super(name, location);
    this.type = requireNonNull(type);
  }

  public TypeS type() {
    return type;
  }
}
