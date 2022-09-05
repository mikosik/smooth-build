package org.smoothbuild.lang.define;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Tapanal;
import org.smoothbuild.lang.type.TypeS;

/**
 * Named value.
 * This class is immutable.
 */
public sealed abstract class NamedValS extends Tapanal implements MonoRefableS
    permits AnnValS, DefValS {
  public NamedValS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}


