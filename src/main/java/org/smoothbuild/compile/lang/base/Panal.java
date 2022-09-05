package org.smoothbuild.compile.lang.base;

import static java.util.Objects.requireNonNull;

import org.smoothbuild.compile.lang.define.ModPath;

/**
 * PANAL = Path and Name and Location.
 */
public class Panal extends NalImpl {
  private final ModPath modPath;

  public Panal(ModPath modPath, String name, Loc loc) {
    super(name, loc);
    this.modPath = requireNonNull(modPath);
  }

  public ModPath modPath() {
    return modPath;
  }
}
