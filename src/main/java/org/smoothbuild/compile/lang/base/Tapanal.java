package org.smoothbuild.compile.lang.base;

import org.smoothbuild.compile.lang.define.ModPath;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.TypelikeS;

/**
 * PATANAL = Path and TypeS and Name and Location.
 */
public class Tapanal extends Panal {
  private final TypeS type;

  public Tapanal(TypeS type, ModPath modPath, String name, Loc loc) {
    super(modPath, name, loc);
    this.type = type;
  }

  public TypelikeS typelike() {
    return type;
  }

  public TypeS type() {
    return type;
  }
}