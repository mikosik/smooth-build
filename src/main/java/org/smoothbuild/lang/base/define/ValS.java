package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * This class is immutable.
 */
public sealed abstract class ValS extends TopEvalS permits BoolValS, DefValS {
  public ValS(TypeS type, ModulePath modulePath, String name, Loc loc) {
    super(type, modulePath, name, loc);
  }
}


