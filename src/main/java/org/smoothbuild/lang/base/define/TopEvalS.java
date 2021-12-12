package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * Top level evaluable.
 */
public sealed abstract class TopEvalS extends EvalS implements Nal
    permits FuncS, DefValS {
  public TopEvalS(TypeS type, ModPath modPath, String name, Loc loc) {
    super(type, modPath, name, loc);
  }
}
