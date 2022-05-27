package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.type.TypeS;

/**
 * PATANAL = Path and TypeS and Name and Location.
 */
public class Patanal extends Tanal {
  private final ModPath modPath;

  public Patanal(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, name, loc);
    this.modPath = requireNonNull(modPath);
  }

  public ModPath modPath() {
    return modPath;
  }
}
