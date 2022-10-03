package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tapanal;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Named value.
 * This class is immutable.
 */
public sealed abstract class ValS extends Tapanal implements NamedEvaluableS
    permits AnnValS, DefValS {
  public ValS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}


