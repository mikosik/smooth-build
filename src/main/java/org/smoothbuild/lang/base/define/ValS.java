package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * This class is immutable.
 */
public sealed abstract class ValS extends TopEvalS permits AnnValS, DefValS {
  public ValS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}


