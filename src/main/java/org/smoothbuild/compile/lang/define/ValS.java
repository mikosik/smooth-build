package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tanal;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Named value.
 * This class is immutable.
 */
public sealed abstract class ValS extends Tanal implements EvaluableS
    permits AnnValS, DefValS {
  public ValS(TypeS type, String name, Loc loc) {
    super(type, name, loc);
  }
}


