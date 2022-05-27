package org.smoothbuild.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.type.TypeS;

/**
 * PATANAL = Path and TypeS and Name and Location.
 */
public class Patanal extends TanalImpl {
  private final ModPath modPath;

  public Patanal(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, name, loc);
    this.modPath = requireNonNull(modPath);
  }

  public ModPath modPath() {
    return modPath;
  }
}
