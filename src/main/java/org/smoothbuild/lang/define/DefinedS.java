package org.smoothbuild.lang.define;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.lang.type.TypeS;

/**
 * This class and all its subclasses are immutable.
 */
public class DefinedS extends TanalS {
  private final ModPath modPath;

  public DefinedS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, name, loc);
    this.modPath = requireNonNull(modPath);
  }

  public ModPath modPath() {
    return modPath;
  }
}
