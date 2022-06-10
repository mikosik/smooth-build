package org.smoothbuild.lang.base;

import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.type.MonoTS;

/**
 * PATANAL = Path and TypeS and Name and Location.
 */
public class Tapanal extends Panal {
  private final MonoTS type;

  public Tapanal(MonoTS type, ModPath modPath, String name, Loc loc) {
    super(modPath, name, loc);
    this.type = type;
  }

  public MonoTS type() {
    return type;
  }
}
